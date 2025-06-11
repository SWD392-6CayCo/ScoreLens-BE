package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModeRequest {
    private String name;
    private String description;
    private boolean isActive;
}
