package com.expsn.cooker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.expsn.cooker.model.Difficulty;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public Recipe getRecipeById(String id, String userId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
        User author = userRepository.findById(recipe.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Autor da receita não encontrado"));

        boolean isOwner = userId != null && recipe.getAuthorId().equals(userId);

        // Todas as receitas de usuários privados são tratadas como privadas para terceiros.
        if (author.isPrivate() && !isOwner) {
            throw new RuntimeException("Você não tem permissão para visualizar esta receita");
        }

        if (!recipe.isPublic() && !isOwner) {
            throw new RuntimeException("Você não tem permissão para visualizar esta receita");
        }

        return recipe;
    }

    public Recipe createRecipe(Recipe recipe, String userId) {
        recipe.setAuthorId(userId);
        recipe.setCreatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(String recipeId, Recipe updatedData, String userId) {
        Recipe existing = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));

        if (!existing.getAuthorId().equals(userId)) {
            throw new RuntimeException("Você não tem permissão para editar esta receita");
        }

        existing.setTitle(updatedData.getTitle());
        existing.setIngredients(updatedData.getIngredients());
        existing.setStepsMD(updatedData.getStepsMD());
        existing.setTags(updatedData.getTags());
        existing.setPublic(updatedData.isPublic());
        existing.setUpdatedAt(LocalDateTime.now());

        return recipeRepository.save(existing);
    }

    public List<Recipe> searchRecipes(String title, List<String> tags, Difficulty diff, String authorHandle, String currentUserId) {
        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users").localField("authorId").foreignField("_id").as("author");

        Criteria criteria = new Criteria();
        
        if (title != null) criteria.and("title").regex(title, "i");
        if (tags != null) criteria.and("tags").all(tags);
        if (diff != null) criteria.and("difficulty").is(diff);
        if (authorHandle != null) criteria.and("author.handle").is(authorHandle.replace("@", ""));

        Criteria visibility = new Criteria();
        if (currentUserId == null) {
            visibility = Criteria.where("isPublic").is(true)
                .and("author.isPrivate").is(false);
        } else {
            visibility.orOperator(
                Criteria.where("authorId").is(currentUserId),
                Criteria.where("isPublic").is(true).and("author.isPrivate").is(false)
            );
        }

        Aggregation agg = Aggregation.newAggregation(
                lookupUser,
                Aggregation.unwind("author"),
            Aggregation.match(new Criteria().andOperator(criteria, visibility))
        );

        return mongoTemplate.aggregate(agg, "recipes", Recipe.class).getMappedResults();
    }

    public List<Recipe> getMyFavoritedRecipes(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getFavoriteRecipeIds().isEmpty()) {
            return List.of();
        }

        return recipeRepository.findAllById(user.getFavoriteRecipeIds());
    }
}