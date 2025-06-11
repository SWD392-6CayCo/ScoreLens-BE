package com.scorelens.DTOs.Response;

import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Enums.ResultStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TeamResponse {
    private int teamID;
    private String name;
    private int totalScore;
    private LocalDateTime createAt;
    private ResultStatus status;
    private BilliardMatch billiardMatch;
}
