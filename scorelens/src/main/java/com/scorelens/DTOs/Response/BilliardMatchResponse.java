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
    private BilliardTable billiardTableID;
    private Mode modeID;
    private Staff byStaff;
    private Customer byCustomer;
    private String winner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalRound;
    private MatchStatus status;
    private String code;

    private List<GameSetResponse> sets;
    private List<TeamResponse> teams;
}
