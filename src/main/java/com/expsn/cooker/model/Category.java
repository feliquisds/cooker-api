package com.expsn.cooker.model;

import java.util.List;

import lombok.Data;

@Data
public class Category extends BookComponent {
    private String name;
    private List<BookComponent> items;
}
