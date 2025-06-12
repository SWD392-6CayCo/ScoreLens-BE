package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MatchStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSetRequest {
    private int gameSetNo;
    private int raceTo;
    private String winner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MatchStatus status;
    private Integer billiardMatchID;

}
