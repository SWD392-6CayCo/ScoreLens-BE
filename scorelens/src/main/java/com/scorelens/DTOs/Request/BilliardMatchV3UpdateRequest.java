package com.scorelens.DTOs.Request;

import com.scorelens.Enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardMatchV3UpdateRequest {
    // For specifying the update operation type
    private String updateType; // "update", "score", "forfeit", "cancel", "complete", "manual"
    
    // For general match update (updateType = "update")
    private String winner;
    private MatchStatus status;
    
    // For score update (updateType = "score")
    // Match ID for operations that need it (cancel, complete, manual, update)
    private Integer matchID;
    private Integer teamID;
    private String delta; // -1, +1
    
    // For forfeit operation (updateType = "forfeit")
    private Integer forfeitTeamID;
}
