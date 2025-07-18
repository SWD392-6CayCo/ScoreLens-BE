package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketV3Request {
    // Required: Type of message to send
    private MessageType messageType; // "notification", "logging", "shot_event"
    
    // Required: Table ID for WebSocket topic
    private String tableID;
    
    // For notification and logging messages
    private String message;
    
    // For shot event messages
    private ShotEvent shotEvent;
}
