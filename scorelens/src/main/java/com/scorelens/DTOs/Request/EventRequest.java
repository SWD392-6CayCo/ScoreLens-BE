package com.scorelens.DTOs.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventRequest {
    private int playerID;
    private int gameSetID;
    private boolean scoreValue;

    @JsonProperty("isFoul")
    private boolean isFoul;

    @JsonProperty("isUncertain")
    private boolean isUncertain;

    private String message;
    private String sceneUrl;
}


