package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.InformationRequest;
import com.scorelens.DTOs.Request.KafkaProducerV3Request;
import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Service.KafkaService.KafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Kafka Producer V3", description = "Unified Kafka Producer API")
@RestController
@RequestMapping("v3/kafka/producers")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerV3Controller {

    KafkaProducer kafkaProducer;

    @Operation(summary = "Send Kafka message with unified parameters", 
               description = "Unified API that combines all POST operations from v1 controller")
    @PostMapping()
    public ResponseObject sendMessage(@RequestBody KafkaProducerV3Request request) {
        try {
            String sendType = request.getSendType();
            if (sendType == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("sendType is required. Valid values: general, information, informationWithTableId, stop")
                        .build();
            }
            
            switch (sendType.toLowerCase()) {
                case "general":
                    return handleGeneralSend(request);
                case "information":
                    return handleInformationSend(request);
                case "informationwithtableid":
                    return handleInformationWithTableIdSend(request);
                case "stop":
                    return handleStopSend(request);
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid sendType. Valid values: general, information, informationWithTableId, stop")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    private ResponseObject handleGeneralSend(KafkaProducerV3Request request) {
        if (request.getProducerRequest() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("ProducerRequest is required for general send operation")
                    .build();
        }
        
        kafkaProducer.sendEvent(request.getProducerRequest());
        return ResponseObject.builder()
                .status(1000)
                .message("Successfully sent message")
                .build();
    }
    
    private ResponseObject handleInformationSend(KafkaProducerV3Request request) {
        if (request.getInformationRequest() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("InformationRequest is required for information send operation")
                    .build();
        }
        
        kafkaProducer.sendEvent(request.getInformationRequest());
        return ResponseObject.builder()
                .status(1000)
                .message("Successfully sent message")
                .build();
    }
    
    private ResponseObject handleInformationWithTableIdSend(KafkaProducerV3Request request) {
        if (request.getInformationRequest() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("InformationRequest is required for information with table ID send operation")
                    .build();
        }
        
        if (request.getTableId() == null || request.getTableId().trim().isEmpty()) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Table ID is required for information with table ID send operation")
                    .build();
        }
        
        kafkaProducer.sendEvent(request.getTableId(), request.getInformationRequest());
        return ResponseObject.builder()
                .status(1000)
                .message("Successfully sent message")
                .build();
    }
    
    private ResponseObject handleStopSend(KafkaProducerV3Request request) {
        kafkaProducer.sendEvent(
                request.getTableId(),
                new ProducerRequest(KafkaCode.STOP_STREAM, request.getTableId(), "Stop stream")
        );
        return ResponseObject.builder()
                .status(1000)
                .message("Successfully sent message")
                .build();
    }
}
