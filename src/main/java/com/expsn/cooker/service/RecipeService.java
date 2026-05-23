package com.expsn.cooker.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.expsn.cooker.exception.BusinessException;
import com.expsn.cooker.exception.ItemException;
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
            .orElseThrow(() -> new ItemException("Receita não encontrada"));
        User author = userRepository.findById(recipe.getAuthorId())
            .orElseThrow(() -> new ItemException("Autor da receita não encontrado"));

        if (!canViewRecipe(recipe, author, userId)) {
            throw new BusinessException("Você não tem permissão para visualizar esta receita");
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
            .orElseThrow(() -> new ItemException("Receita não encontrada"));

        if (!isOwner(existing.getAuthorId(), userId)) {
            throw new BusinessException("Você não tem permissão para editar esta receita");
        }

        existing.setTitle(updatedData.getTitle());
        existing.setDifficulty(updatedData.getDifficulty());
        existing.setTimeMinutes(updatedData.getTimeMinutes());
        existing.setImages(updatedData.getImages());
        existing.setPortions(updatedData.getPortions());
        existing.setDescriptionMD(updatedData.getDescriptionMD());
        existing.setIngredientSections(updatedData.getIngredientSections());
        existing.setStepsMD(updatedData.getStepsMD());
        existing.setTags(updatedData.getTags());
        existing.setPublic(updatedData.isPublic());
        existing.setUpdatedAt(LocalDateTime.now());

        return recipeRepository.save(existing);
    }

    public List<Recipe> searchRecipes(String title, List<String> tags, Difficulty diff, String authorHandle, String currentUserId) {
        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users").localField("authorId").foreignField("_id").as("author");

        List<Criteria> criteriaList = new ArrayList<>();
        if (title != null && !title.isBlank()) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }
        if (tags != null && !tags.isEmpty()) {
            criteriaList.add(Criteria.where("tags").all(tags));
        }
        if (diff != null) {
            criteriaList.add(Criteria.where("difficulty").is(diff));
        }
        if (authorHandle != null && !authorHandle.isBlank()) {
            criteriaList.add(Criteria.where("author.handle").is(normalizeHandle(authorHandle)));
        }

        criteriaList.add(buildVisibilityCriteria(currentUserId));

        Criteria matchCriteria = criteriaList.size() == 1
                ? criteriaList.get(0)
                : new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        Aggregation agg = Aggregation.newAggregation(
                lookupUser,
                Aggregation.unwind("author"),
                Aggregation.match(matchCriteria)
        );

        return mongoTemplate.aggregate(agg, "recipes", Recipe.class).getMappedResults();
    }

    public List<Recipe> getMyFavoritedRecipes(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getFavoriteRecipeIds() == null || user.getFavoriteRecipeIds().isEmpty()) {
            return List.of();
        }

        return recipeRepository.findAllById(user.getFavoriteRecipeIds()).stream()
                .filter(recipe -> {
                    User author = userRepository.findById(recipe.getAuthorId())
                            .orElseThrow(() -> new RuntimeException("Autor da receita não encontrado"));
                    return canViewRecipe(recipe, author, userId);
                })
                .toList();
    }

    private boolean canViewRecipe(Recipe recipe, User author, String userId) {
        if (isOwner(recipe.getAuthorId(), userId)) {
            return true;
        }

        return recipe.isPublic() && !author.isPrivate();
    }

    private Criteria buildVisibilityCriteria(String currentUserId) {
        if (currentUserId == null) {
            return Criteria.where("isPublic").is(true)
                    .and("author.isPrivate").is(false);
        }

        return new Criteria().orOperator(
                Criteria.where("authorId").is(currentUserId),
                Criteria.where("isPublic").is(true).and("author.isPrivate").is(false)
        );
    }

    private boolean isOwner(String ownerId, String userId) {
        return userId != null && ownerId.equals(userId);
    }

    private String normalizeHandle(String handle) {
        return handle.replace("@", "").trim();
    }
}