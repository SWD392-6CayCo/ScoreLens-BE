package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSetUpdateRequest {
    //private int gameSetNo;
    private Integer raceTo;
    private String winner;
    //private LocalDateTime startTime;
    //private LocalDateTime endTime;
    private MatchStatus status;
    //private Integer billiardMatchID;

}
