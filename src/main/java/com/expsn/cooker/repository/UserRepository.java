package com.expsn.cooker.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expsn.cooker.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByHandle(String handle);
    Optional<User> findByEmail(String email);
}
