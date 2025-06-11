package com.scorelens.DTOs.Request;

import com.scorelens.Enums.ResultStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TeamRequest {
    private String name;
    private int totalScore;
    private LocalDateTime createAt;
    private ResultStatus status;
    private Integer billiardMatchID;
}

