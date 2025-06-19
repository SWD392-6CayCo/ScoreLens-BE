package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSetUpdateRequest {
    private Integer raceTo;
    private String winner;
    private MatchStatus status;
}
