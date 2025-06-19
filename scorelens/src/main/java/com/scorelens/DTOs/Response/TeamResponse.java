package com.scorelens.DTOs.Response;

import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.Player;
import com.scorelens.Enums.ResultStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TeamResponse {
    private Integer billiardMatchID;
    private int teamID;
    private String name;
    private int totalMember;
    private int totalScore;
    private LocalDateTime createAt;
    private ResultStatus status;

    private List<PlayerResponse> players;
}
