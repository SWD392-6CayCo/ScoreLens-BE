package com.scorelens.Config;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Request.LogMessageRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

//    *******************************kafka config************************************************************

    private Map<String, Object> commonKafkaSSLProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-5c346d1-kafka-scorelens.f.aivencloud.com:26036");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "scorelens-group");
        // deserializer cho key và value của kafka
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("security.protocol", "SSL");
        props.put("ssl.truststore.location", "certs/client.truststore.jks");
        props.put("ssl.truststore.password", "123456");
        props.put("ssl.keystore.type", "PKCS12");
        props.put("ssl.keystore.location", "certs/client.keystore.p12");
        props.put("ssl.keystore.password", "123456");
        props.put("ssl.key.password", "123456");
        return props;
    }

//    ********************************           CONSUMER           ********************************************************

    //    ********************************json message********************************************************
    @Bean
    public ConsumerFactory<String, LogMessageRequest> jsonConsumerFactory() {
        Map<String, Object> props = commonKafkaSSLProps();
        JsonDeserializer<LogMessageRequest> deserializer = new JsonDeserializer<>(LogMessageRequest.class);
        deserializer.addTrustedPackages("*");

        // type headers
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LogMessageRequest> jsonKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LogMessageRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonConsumerFactory());
        return factory;
    }

    //    ********************************ai-noti message********************************************************
//    @Bean
//    public ConsumerFactory<String, EventRequest> aiNotiConsumerFactory() {
//        Map<String, Object> props = commonKafkaSSLProps();
//        JsonDeserializer<EventRequest> deserializer = new JsonDeserializer<>(EventRequest.class);
//        deserializer.addTrustedPackages("*");
//
//        // type headers
//        deserializer.setRemoveTypeHeaders(false);
//        deserializer.setUseTypeMapperForKey(false);
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
//    }
//
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, EventRequest> aiNotiKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, EventRequest> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(aiNotiConsumerFactory());
//        return factory;
//    }

    

























}
