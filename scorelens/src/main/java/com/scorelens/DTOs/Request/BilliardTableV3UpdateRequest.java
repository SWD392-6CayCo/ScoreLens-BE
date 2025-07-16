package com.scorelens.DTOs.Request;

import com.scorelens.Enums.TableStatus;
import com.scorelens.Enums.TableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardTableV3UpdateRequest {
    // For specifying the update operation type
    private String updateType; // "update", "status"
    
    // Table ID for all operations
    private String id;
    
    // For general table update (updateType = "update")
    private TableType tableType;
    private String name;
    private String description;
    private TableStatus status;
    private String cameraUrl;
    private boolean isActive;
    private String storeID;
    
    // For status update only (updateType = "status")
    private TableStatus statusValue; // The new status value
}
