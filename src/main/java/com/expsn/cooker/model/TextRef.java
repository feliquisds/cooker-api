package com.expsn.cooker.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextRef extends BookComponent {
    private String textId;
    private String title;
}
