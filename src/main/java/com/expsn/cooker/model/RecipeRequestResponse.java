package com.expsn.cooker.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Data
public class RecipeRequestResponse {
    @Field(targetType = FieldType.OBJECT_ID)
    private String responderId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String recipeId;
    private String messageMD;
    private LocalDateTime createdAt;
}
