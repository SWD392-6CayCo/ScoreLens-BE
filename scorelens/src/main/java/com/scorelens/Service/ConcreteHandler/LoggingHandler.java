package com.scorelens.Service.ConcreteHandler;

import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Service.EventProcessorService;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import com.scorelens.Service.Interface.customAnnotation.KafkaCodeMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
@KafkaCodeMapping(KafkaCode.LOGGING)
public class LoggingHandler implements KafkaCodeHandler {

    @Autowired
    private EventProcessorService eventProcessorService;

    @Override
    public void handle(ProducerRequest request) {
        eventProcessorService.processEvent(request);
    }

}
