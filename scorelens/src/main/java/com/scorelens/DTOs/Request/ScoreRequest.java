package com.scorelens.DTOs.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoreRequest {
    private Integer matchID;
    private Integer teamID;
    private String delta;     // -1, +1
}
