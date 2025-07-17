package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FCMNotificationV3Request {
    // Required: Type of FCM operation
    private String operationType; // "send", "register"
    
    // Required: Table ID for FCM operations
    private String tableID;
    
    // For send notification operation
    private String title;
    private String body;
    
    // For register token operation
    private String token;

}
