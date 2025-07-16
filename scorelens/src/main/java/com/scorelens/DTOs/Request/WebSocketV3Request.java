package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketV3Request {
    // For specifying the WebSocket operation type
    private String messageType; // "notification", "logging", "shotEvent"
    
    // For notification and logging operations (messageType = "notification" or "logging")
    private String message;
    
    // For shot event operation (messageType = "shotEvent")
    private ShotEvent shotEvent;
    
    // Note: Each operation sends to a different WebSocket topic
}
