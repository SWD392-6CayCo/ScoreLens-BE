package com.scorelens.Service.ConcreteHandler;

import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import com.scorelens.Service.Interface.customAnnotation.KafkaCodeMapping;
import com.scorelens.Service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
@KafkaCodeMapping(KafkaCode.DELETE_CONFIRM)
public class DeleteConfirmHandler implements KafkaCodeHandler {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void handle(ProducerRequest request) {
        String tableID = request.getTableID();
        int deleteCount = (Integer) request.getData();
        webSocketService.sendToWebSocket(
                WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
                new WebsocketReq(WebSocketCode.WARNING, "Delete Event count: " + deleteCount)
        );
    }
}
