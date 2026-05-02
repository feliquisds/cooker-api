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
import com.expsn.cooker.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MongoTemplate mongoTemplate;

    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
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

    public List<Recipe> searchRecipes(String title, List<String> tags, Difficulty diff, String authorHandle) {
        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users").localField("authorId").foreignField("_id").as("author");

        Criteria criteria = new Criteria();
        
        if (title != null) criteria.and("title").regex(title, "i");
        if (tags != null) criteria.and("tags").all(tags);
        if (diff != null) criteria.and("difficulty").is(diff);
        if (authorHandle != null) criteria.and("author.handle").is(authorHandle.replace("@", ""));

        // Regras de Visibilidade Pública
        criteria.and("isPublic").is(true);          // A receita precisa ser pública
        criteria.and("author.isPrivate").is(false); // O autor NÃO pode ser privado

        Aggregation agg = Aggregation.newAggregation(
                lookupUser,
                Aggregation.unwind("author"),
                Aggregation.match(criteria)
        );

        return mongoTemplate.aggregate(agg, "recipes", Recipe.class).getMappedResults();
    }
}