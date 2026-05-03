package com.expsn.cooker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPublic {
    private String name;
    private String handle;
    private String bio;
    private String avatarUrl;
}
