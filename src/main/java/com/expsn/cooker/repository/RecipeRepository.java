package com.expsn.cooker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expsn.cooker.model.Recipe;

public interface RecipeRepository extends MongoRepository<Recipe, String> {
    List<Recipe> findByAuthorId(String authorId);
    List<Recipe> findByTagsInAndIsPublicTrue(List<String> tags);
    List<Recipe> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title);
    List<Recipe> findByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime start, LocalDateTime end);
    
}
