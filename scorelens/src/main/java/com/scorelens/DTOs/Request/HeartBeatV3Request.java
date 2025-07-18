package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeartBeatV3Request {
    // For specifying the heartbeat operation type
    private String operationType; // "send", "start", "stop"
    
    // Table ID for send operation (operationType = "send")
    private String tableId;
    
    // Note: For "start" and "stop" operations, no additional data is needed
}
