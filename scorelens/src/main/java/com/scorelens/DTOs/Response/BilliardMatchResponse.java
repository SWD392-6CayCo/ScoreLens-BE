package com.scorelens.DTOs.Response;

import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Mode;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.MatchStatus;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardMatchResponse {
    private Integer billiardMatchID;
    private String billiardTableID;
    private Integer modeID;
    private String byStaff;
    private String byCustomer;
    private String setUp;
    private String winner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalSet;
    private String status;
    private String code;

    private List<GameSetResponse> sets;
    private List<TeamResponse> teams;
}
