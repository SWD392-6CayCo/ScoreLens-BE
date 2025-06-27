package com.scorelens.DTOs.Response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamSetResponse {
    private int teamSetID;
    private int teamID;
    private int gameSetID;
    private int totalScore;
}
