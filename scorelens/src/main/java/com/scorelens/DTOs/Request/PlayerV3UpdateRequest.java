package com.scorelens.DTOs.Request;

import com.scorelens.Enums.ResultStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerV3UpdateRequest {
    // For specifying the update operation type
    private String updateType; // "update", "saveCustomer"
    
    // Player ID for all operations
    private Integer id;
    
    // For general player update (updateType = "update")
    private String name;
    private Integer totalScore;
    private ResultStatus status;
    private String customerID;
    
    // For save customer operation (updateType = "saveCustomer")
    private String info; // email or phone number
}
