package com.scorelens.Service.KafkaService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.DTOs.Request.*;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Service.ConcreteCreator.KafkaCodeHandlerFactory;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    ObjectMapper mapper = new ObjectMapper();

    KafkaCodeHandlerFactory handlerFactory;

    // msg từ fastapi
    @KafkaListener(
            topics = "py_to_ja",
            containerFactory = "StringKafkaListenerContainerFactory"
    )
    public void listenCommunication(
            String message,                                                                           // value
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,                  // key
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            Acknowledgment ack
    ) {
        try {
            ProducerRequest request = mapper.readValue(message, ProducerRequest.class);
            log.info("Received message on partition {} with key {}: {}", partition, key, message);
            log.info("KafkaCode received: {}", request.getCode());
            log.info("Data type: {}", request.getData().getClass());

            handlingKafkaCode(request);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ProducerRequest: {}", message, e);
        }
    }


    //xử lí enum KafkaCode
    private void handlingKafkaCode(ProducerRequest request) {
        KafkaCode code = request.getCode();
        try {

            //tra ve handler voi kafkaCode tuong ung trong factory
            KafkaCodeHandler handler = handlerFactory.getKafkaCodeHandler(code);
            if (handler != null) {
                handler.handle(request);
            } else {
                log.warn("No handler found for KafkaCode: {}", code);
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid KafkaCode: " + code);
        }
    }


}
