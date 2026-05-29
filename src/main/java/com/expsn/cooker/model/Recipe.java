package com.expsn.cooker.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Document(collection = "recipes")
@Data
@Builder
@AllArgsConstructor
public class Recipe {
    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String authorId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String bookOriginId;
    
    private String title;
    private List<String> images;
    private List<String> tags;
    
    private Difficulty difficulty;
    private Integer timeMinutes;
    private Integer portions;
    
    private String descriptionMD;
    private List<IngredientSection> ingredientSections;
    private List<String> stepsMD;
    
    private boolean isPublic;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private double rating; // average rating from reviews
}
