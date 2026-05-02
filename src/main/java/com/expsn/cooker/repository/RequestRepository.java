package com.expsn.cooker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expsn.cooker.model.RecipeRequest;

public interface RequestRepository extends MongoRepository<RecipeRequest, String> {

    List<RecipeRequest> findByCreatedAtAfterAndManuallyClosedFalse(LocalDateTime limit);
    
}