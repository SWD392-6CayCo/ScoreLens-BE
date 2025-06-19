package com.scorelens.DTOs.Request;

import lombok.Data;

@Data
public class ProducerRequest {
    private String producerName;
    private int playerID;
    private int gameSetID;

}
