package com.expsn.cooker.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BookComponent {
    private String name;
    private List<BookComponent> items;
}
