package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {
    private int eventID;
    private int gameSetID;
    private int playerID;
    private boolean scoreValue;
    private boolean isFoul;
    private boolean isUncertain;
    private LocalDateTime timeStamp;
    private String message;
    private String sceneUrl;
}
