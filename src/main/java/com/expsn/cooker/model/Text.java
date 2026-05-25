package com.expsn.cooker.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Builder;
import lombok.Data;

@Document(collection = "texts")
@Data
@Builder
public class Text {
    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String authorId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String bookOriginId;
    
    private String title;
    private List<String> tags;
    
    private String contentMD;
    
    private boolean isPublic;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private double rating; // average rating from reviews
}
