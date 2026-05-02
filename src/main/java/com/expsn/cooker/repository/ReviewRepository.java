package com.expsn.cooker.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expsn.cooker.model.AIStatus;
import com.expsn.cooker.model.Review;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByTargetIdAndAiStatus(String targetId, AIStatus status);
    List<Review> findByAuthorId(String authorId);
    List<Review> findByTargetIdAndAuthorId(String recipeId, String authorId);
}
