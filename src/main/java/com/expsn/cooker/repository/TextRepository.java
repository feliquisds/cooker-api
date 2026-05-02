package com.expsn.cooker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expsn.cooker.model.Text;

public interface TextRepository extends MongoRepository<Text, String> {
    
}
