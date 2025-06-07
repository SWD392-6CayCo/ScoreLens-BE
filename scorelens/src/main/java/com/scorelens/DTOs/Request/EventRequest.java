package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequest {
    private int playerID;
    private int roundID;
    private boolean scoreValue;
    private boolean isFoul;
    private boolean isUncertain;
    private String message;
    private String sceneUrl;
}
