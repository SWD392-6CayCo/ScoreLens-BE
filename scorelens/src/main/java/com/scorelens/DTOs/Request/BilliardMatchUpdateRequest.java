package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardMatchUpdateRequest {
    private String winner;
    private MatchStatus status;
}
