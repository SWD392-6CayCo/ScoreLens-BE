package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamCreateRequest {
    private Integer billiardMatchID;
    private String name;
    private Integer totalMember;
    private List<String> memberNames;
}

