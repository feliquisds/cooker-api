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

@Document(collection = "requests")
@Data
@Builder
public class RecipeRequest {
    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String requesterId;
    private String title;
    private String descriptionMD;
    private List<String> tags;
    
    private List<RecipeRequestResponse> responses;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    private boolean manuallyClosed = false;

    public boolean isActive() {
        return !manuallyClosed && createdAt.plusDays(30).isAfter(LocalDateTime.now());
    }
}
