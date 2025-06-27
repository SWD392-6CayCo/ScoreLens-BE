package com.scorelens.Config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

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
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        props.put("security.protocol", "SSL");
        props.put("ssl.truststore.location", "certs/client.truststore.jks");
        props.put("ssl.truststore.password", "123456");
        props.put("ssl.keystore.type", "PKCS12");
        props.put("ssl.keystore.location", "certs/client.keystore.p12");
        props.put("ssl.keystore.password", "123456");
        props.put("ssl.key.password", "123456");
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> StringConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(commonKafkaSSLProps());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> StringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(StringConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }


}
