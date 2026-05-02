package com.expsn.cooker.model;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Data
public class RecipeRef extends BookComponent {
    @Field(targetType = FieldType.OBJECT_ID)
    private String recipeId;
    private String title;
}
