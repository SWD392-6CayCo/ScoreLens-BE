package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ScoreRequest {
    private Integer matchID;
    private Integer teamID;
    private String delta;     // -1, +1
}
