package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MatchFilterRequest {
    private Date date;
    private String status;
    private Integer modeID;
}
