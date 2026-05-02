package com.expsn.cooker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPublicDTO {
    private String name;
    private String handle;
    private String bio;
    private String avatarUrl;
}
