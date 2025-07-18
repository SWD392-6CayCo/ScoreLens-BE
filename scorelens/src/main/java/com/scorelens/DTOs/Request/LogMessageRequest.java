package com.scorelens.DTOs.Request;

import lombok.Data;

import java.util.List;

@Data
public class LogMessageRequest {
    private int cueBallId;
    private int targetBallId;
    private int modeID;
    private String message;
    private EventRequest details;
}
