package com.expsn.cooker.model;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Data
public class BookItem {
    private ItemType type;

    @Field(targetType = FieldType.OBJECT_ID)
    private String itemId;
}
