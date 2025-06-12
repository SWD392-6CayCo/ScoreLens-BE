package com.scorelens.DTOs.Request;

import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Mode;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.MatchStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardMatchRequest {
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

}
