package com.scorelens.Service.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final int partition = 0;

    public void sendEvent(String topic, Object object) {
        kafkaTemplate.send(topic, partition, null, object);
        kafkaTemplate.flush();
        log.info("Sending message: {}", object);
    }



}
