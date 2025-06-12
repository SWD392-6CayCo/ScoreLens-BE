package com.scorelens.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "scorelens", groupId = "scorelens-group")
    public void listen(String message) {
        System.out.println("Received message via SSL KafkaListener: " + message);
    }
}
