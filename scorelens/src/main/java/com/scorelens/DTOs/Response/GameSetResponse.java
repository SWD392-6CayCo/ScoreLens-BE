package com.scorelens.DTOs.Response;

import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GameSetResponse {
    private int gameSetID;
    private int gameSetNo;
    private int raceTo;
    private String winner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MatchStatus status;
    private BilliardMatch billiardMatch;
}
