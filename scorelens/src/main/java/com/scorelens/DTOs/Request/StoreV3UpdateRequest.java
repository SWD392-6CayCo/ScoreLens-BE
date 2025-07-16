package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreV3UpdateRequest {
    // For specifying the update operation type
    private String updateType; // "update", "status"
    
    // Store ID for all operations
    private String id;
    
    // For general store update (updateType = "update")
    private String name;
    private String address;
    private String status;
    private String description;
    
    // For status update only (updateType = "status")
    private String statusValue; // The new status value
}
