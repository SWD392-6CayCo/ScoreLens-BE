package com.scorelens.Service.Interface;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.DTOs.Request.ProducerRequest;

public interface KafkaCodeHandler {
    void handle(ProducerRequest request) throws FirebaseMessagingException;
}
