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

@Document(collection = "recipe_books")
@Data
@Builder
@AllArgsConstructor
public class RecipeBook {
    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String ownerId;
    private String title;
    private String descriptionMD;
    private List<String> tags;
    private boolean isPublic;

    private List<BookComponent> items;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private double rating; // average rating from reviews of all contained items
}
