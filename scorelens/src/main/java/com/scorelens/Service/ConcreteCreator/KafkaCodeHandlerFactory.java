package com.scorelens.Service.ConcreteCreator;

import com.scorelens.Enums.KafkaCode;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import com.scorelens.Service.Interface.customAnnotation.KafkaCodeMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaCodeHandlerFactory implements KafkaCodeHandlerCreator{
    private final Map<KafkaCode, KafkaCodeHandler> handlerMap = new HashMap<>();

    //inject tat ca ca bean implemented KafkaCodeHandler (Running, Logging, Deleting) vao list handlers
    @Autowired
    public KafkaCodeHandlerFactory(List<KafkaCodeHandler> handlers) {

//        List<KafkaCodeHandler> handlers = [
//        RunningHandler instance,
//        LoggingHandler instance,
//        DeleteConfirmHandler instance ]


        for (KafkaCodeHandler handler : handlers) {
            //lay class cua handler
            //lay annotation trong @KafkaCodeMapping
            KafkaCodeMapping annotation = handler.getClass().getAnnotation(KafkaCodeMapping.class);
            if (annotation != null) {
                handlerMap.put(annotation.value(), handler);

//                handlerMap.put(KafkaCode.RUNNING, RunningHandler instance)
            }
        }
    }


    //tra ve handler voi KafkaCode tuong ung
    //day la factory method
    @Override
    public KafkaCodeHandler getKafkaCodeHandler(KafkaCode code) {
        return handlerMap.get(code);
    }
}
