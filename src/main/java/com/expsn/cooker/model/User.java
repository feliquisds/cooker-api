package com.expsn.cooker.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "users")
@Data
@Builder
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String handle;

    private String name;
    private String email;
    private String password;
    private String avatarUrl;
    private String bio;
    private LocalDate birthDate;
    private boolean isPrivate;
    
    private List<String> recipeNotificationTags;
    private List<String> requestNotificationTags;

    private List<String> favoriteRecipeIds;
    private List<String> savedBookIds;

    private double rating; // average rating from reviews of all authored items
}
