package com.expsn.cooker.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Builder;
import lombok.Data;

@Document(collection = "reviews")
@Data
@Builder
public class Review {
    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String targetId; // ID da Receita ou Texto

    @Field(targetType = FieldType.OBJECT_ID)
    private String authorId;
    
    private String title;
    private String contentMD;
    private List<String> images;
    private Integer rating;
    
    private AIStatus aiStatus; // Moderação automática
}
