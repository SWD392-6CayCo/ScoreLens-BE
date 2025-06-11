package com.scorelens.DTOs.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModeResponse {
    private Integer modeID;
    private String name;
    private String description;
    private boolean isActive;
}
