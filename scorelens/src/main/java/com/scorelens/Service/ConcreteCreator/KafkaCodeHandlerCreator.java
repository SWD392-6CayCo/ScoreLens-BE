package com.scorelens.Service.ConcreteCreator;

import com.scorelens.Enums.KafkaCode;
import com.scorelens.Service.Interface.KafkaCodeHandler;

public interface KafkaCodeHandlerCreator {
    KafkaCodeHandler getKafkaCodeHandler(KafkaCode code);
}
