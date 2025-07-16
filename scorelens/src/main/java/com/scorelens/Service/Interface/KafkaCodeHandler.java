package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.ProducerRequest;

public interface KafkaCodeHandler {
    void handle(ProducerRequest request);
}
