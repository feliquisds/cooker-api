package com.expsn.cooker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ingredient {
    private double quantity;
    private String unit;
    private String name;
}
