package com.expsn.cooker.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Builder;
import lombok.Data;

@Document(collection = "recipe_books")
@Data
@Builder
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

    private double rating; // average rating from reviews of all contained items
}
