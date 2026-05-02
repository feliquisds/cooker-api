package com.expsn.cooker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expsn.cooker.model.RecipeBook;

public interface RecipeBookRepository extends MongoRepository<RecipeBook, String> {
    
}
