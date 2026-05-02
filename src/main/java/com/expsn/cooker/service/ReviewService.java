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
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;
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

        // Se a receita for pública, mostramos todos os reviews aprovados pela IA
        if (recipe.isPublic()) {
            return reviewRepository.findByTargetIdAndAiStatus(recipeId, Status.APPROVED);
        }

        // Se a receita for privada:
        if (currentUserId == null) return Collections.emptyList(); // Visitante não vê nada privado

        // Se o usuário logado for o dono da receita, ele vê todos os reviews nela
        if (recipe.getAuthorId().equals(currentUserId)) {
            return reviewRepository.findByTargetIdAndAiStatus(recipeId, Status.APPROVED);
        }

        // Caso contrário, o usuário logado só vê o PRÓPRIO review (se ele tiver feito um)
        return reviewRepository.findByTargetIdAndAuthorId(recipeId, currentUserId);
    }

    public List<Review> getReviewsFromUser(String userId) {
        return reviewRepository.findByAuthorId(userId);
    }

    public List<Review> getReviewsForRecipe(String recipeId) {
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
}
