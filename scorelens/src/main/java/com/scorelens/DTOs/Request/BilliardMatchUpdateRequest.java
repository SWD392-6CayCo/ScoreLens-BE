package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardMatchUpdateRequest {
//    private String billiardTableID;
//    private Integer modeID;
//    private String staffID;
//    private String customerID;
//    private String setUp;
    private String winner;
    //private LocalDateTime startTime;
    //private LocalDateTime endTime;
    //private Integer totalSet;
    private MatchStatus status;
    //private String code;
}
