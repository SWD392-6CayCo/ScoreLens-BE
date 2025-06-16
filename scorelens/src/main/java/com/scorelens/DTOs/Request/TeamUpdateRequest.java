package com.scorelens.DTOs.Request;

import com.scorelens.Enums.ResultStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamUpdateRequest {
    private String name;
    private Integer totalMember;
    private Integer totalScore;
    private ResultStatus status;
}

