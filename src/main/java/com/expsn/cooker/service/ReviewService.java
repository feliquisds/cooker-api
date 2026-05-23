package com.expsn.cooker.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.expsn.cooker.model.Status;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.Review;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.ReviewRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    // Aqui você injetaria um cliente para o serviço de IA (OpenAI, Gemini, etc.)
    // private final AIService aiService; 

    public Review submitReview(Review review) {
        if (review.getContentMD() == null || review.getContentMD().isEmpty()
            || review.getImages() == null || review.getImages().isEmpty()
            || review.getRating() == null) {
            throw new RuntimeException("O conteúdo do review é obrigatório");
        }

        // Simulação da Regra de Negócio: Revisado por IA
        // No mundo real, você faria uma chamada assíncrona ou esperaria o retorno da IA
        review.setAiStatus(Status.APPROVED);
        return reviewRepository.save(review);
    }

    public List<Review> getVisibleReviews(String recipeId, String currentUserId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
        User recipeAuthor = userRepository.findById(recipe.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Autor da receita não encontrado"));

        if (isRecipePubliclyVisible(recipe, recipeAuthor)) {
            return reviewRepository.findByTargetIdAndAiStatus(recipeId, Status.APPROVED);
        }

        if (currentUserId != null && currentUserId.equals(recipe.getAuthorId())) {
            return reviewRepository.findByTargetIdAndAiStatus(recipeId, Status.APPROVED);
        }

        throw new RuntimeException("Acesso negado a esta receita");
    }

    public List<Review> getReviewsFromUser(String userId) {
        return reviewRepository.findByAuthorId(userId);
    }

    public List<Review> getReviewsForRecipe(String recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
        User recipeAuthor = userRepository.findById(recipe.getAuthorId())
            .orElseThrow(() -> new RuntimeException("Autor da receita não encontrado"));

        if (!isRecipePubliclyVisible(recipe, recipeAuthor)) {
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

    private boolean isRecipePubliclyVisible(Recipe recipe, User author) {
        return recipe.isPublic() && !author.isPrivate();
    }
}
