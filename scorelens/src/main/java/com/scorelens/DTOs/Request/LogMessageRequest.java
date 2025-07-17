package com.scorelens.DTOs.Request;

import lombok.Data;

import java.util.List;

@Data
public class LogMessageRequest {
    private String timestamp;
    private String service;
    private String level;
    private String type;
    private String trace_id;
    private int cueBallId;
    private int targetBallId;
    private int modeID;
    private List<Ball> balls;
    private List<Collision> collisions;
    private String message;
    private EventRequest details;
}
