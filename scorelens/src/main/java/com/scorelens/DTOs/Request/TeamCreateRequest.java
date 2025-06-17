package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequest {
    private Integer billiardMatchID;
    private String name;
    private Integer totalMember;
}

