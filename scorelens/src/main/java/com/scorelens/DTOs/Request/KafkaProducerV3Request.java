package com.scorelens.DTOs.Request;

import com.scorelens.Enums.KafkaCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaProducerV3Request {
    // For specifying the send operation type
    private String sendType; // "general", "information", "informationWithTableId", "stop"
    
    // Table ID for operations that need it (sendType = "informationWithTableId")
    private String tableId;
    
    // For general send operation (sendType = "general")
    private ProducerRequest producerRequest;
    
    // For information send operations (sendType = "information" or "informationWithTableId")
    private InformationRequest informationRequest;
    
    // Note: For "stop" operation, no additional data is needed as it creates a fixed ProducerRequest internally
}
