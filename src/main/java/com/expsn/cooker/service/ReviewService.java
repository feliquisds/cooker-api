package com.expsn.cooker.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.expsn.cooker.client.AIClient;
import com.expsn.cooker.exception.BusinessException;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.model.Review;
import com.expsn.cooker.model.User;
import com.expsn.cooker.exception.ItemException;
import com.expsn.cooker.model.Status;
import com.expsn.cooker.repository.RecipeBookRepository;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.ReviewRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeBookRepository recipeBookRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final AIClient aiClient;

    public Review save(String recipeId, Review review, String userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ItemException("Receita não encontrada"));

        if (!isRecipePubliclyVisible(recipe)) {
            throw new BusinessException("Acesso negado a esta receita");
        }

        if (review.getContentMD() == null || review.getContentMD().isEmpty()
            || review.getImages() == null || review.getImages().isEmpty()
            || review.getRating() == null) {
            throw new BusinessException("O conteúdo do review é obrigatório");
        }

        review.setTargetId(recipeId);
        review.setAuthorId(userId);
        review.setAiStatus(Status.PENDING);

        Review savedReview = reviewRepository.save(review);
        aiClient.queueForAnalysis(savedReview);
        updateRanking(recipeId);
        return savedReview;
    }

    public List<Review> getVisibleReviews(String recipeId, String currentUserId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ItemException("Receita não encontrada"));
        RecipeBook recipeBook = recipeBookRepository.findById(recipe.getBookOriginId())
                .orElseThrow(() -> new ItemException("Livro de receitas não encontrado"));
        User recipeAuthor = userRepository.findById(recipe.getAuthorId())
                .orElseThrow(() -> new ItemException("Autor da receita não encontrado"));

        if ((recipeAuthor.isPrivate() || !recipe.isPublic() || !recipeBook.isPublic()) &&
            (currentUserId == null || !recipeAuthor.getId().equals(currentUserId))) {
            throw new BusinessException("Acesso negado a esta receita");
        }

        return reviewRepository.findByTargetIdAndAiStatus(recipeId, Status.APPROVED);
    }

    public List<Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByAuthorIdAndAiStatus(userId, Status.APPROVED);
    }

    public List<Review> getMyReviews(String userId) {
        return reviewRepository.findByAuthorId(userId);
    }

    public List<Review> getReviewsForRecipe(String recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ItemException("Receita não encontrada"));

        if (!isRecipePubliclyVisible(recipe)) {
            return Collections.emptyList();
        }

        // 1. Fazemos um lookup para trazer os dados do autor de cada review
        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users").localField("authorId").foreignField("_id").as("author");

        Criteria criteria = new Criteria();
        criteria.and("targetId").is(recipeId);
        criteria.and("aiStatus").is(Status.APPROVED);
        
        // REGRA: Só mostra o review se o autor NÃO for privado
        criteria.and("author.isPrivate").is(false);

        Aggregation agg = Aggregation.newAggregation(
                lookupUser,
                Aggregation.unwind("author"),
                Aggregation.match(criteria)
        );

        return mongoTemplate.aggregate(agg, "reviews", Review.class).getMappedResults();
    }

    public void updateStatus(String reviewId, Status status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ItemException("Review não encontrado"));

        review.setAiStatus(status);
        reviewRepository.save(review);
        updateRanking(review.getTargetId());
    }

    public void updateRanking(String recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ItemException("Receita não encontrada"));

        List<Review> reviews = reviewRepository.findByTargetIdAndAiStatus(recipeId, Status.APPROVED);
        if (reviews.isEmpty()) {
            recipe.setRating(0);
        } else {
            double total = reviews.stream().mapToInt(Review::getRating).sum();
            recipe.setRating(total / reviews.size());
        }

        recipeRepository.save(recipe);
    }

    private boolean isRecipePubliclyVisible(Recipe recipe) {
        var originBook = recipeBookRepository.findById(recipe.getBookOriginId())
                .orElseThrow(() -> new ItemException("Livro de receitas não encontrado"));
        var user = userRepository.findById(recipe.getAuthorId())
                .orElseThrow(() -> new ItemException("Autor da receita não encontrado"));
        return recipe.isPublic() && !user.isPrivate() && originBook.isPublic();
    }
}
