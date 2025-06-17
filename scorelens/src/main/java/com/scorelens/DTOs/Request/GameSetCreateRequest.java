package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSetCreateRequest {
    private Integer billiardMatchID;
    private Integer raceTo;
}
