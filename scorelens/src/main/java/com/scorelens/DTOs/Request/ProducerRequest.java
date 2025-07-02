package com.scorelens.DTOs.Request;

import com.scorelens.Enums.KafkaCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProducerRequest {
    private KafkaCode code;
    private String tableID;
    private Object data;

}
