package com.scorelens.DTOs.Request;

import lombok.Data;

@Data
public class EventRequest {
    private int playerID;
    private int gameSetID;
    private boolean scoreValue;
    private boolean isFoul;
    private boolean isUncertain;
    private String message;
    private String sceneUrl;
}


