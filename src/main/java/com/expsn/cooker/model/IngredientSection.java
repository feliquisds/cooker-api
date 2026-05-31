package com.expsn.cooker.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IngredientSection {
    private String title; // null or "default" for single-list recipes
    private List<String> ingredients;
}
