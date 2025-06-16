package com.scorelens.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.DTOs.Request.KafkaMessageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final ObjectMapper mapper = new ObjectMapper();

    // json message
    @KafkaListener(
            topics = "scorelens",
            groupId = "scorelens-group",
            containerFactory = "jsonKafkaListenerContainerFactory"
    )
    public void listenJson(KafkaMessageRequest message) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
            System.out.println("Received JSON message via SSL KafkaListener:\n" + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
