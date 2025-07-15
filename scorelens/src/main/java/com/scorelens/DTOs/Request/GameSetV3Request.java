package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSetV3Request {
    // For specifying query type
    private String queryType; // "byId", "byMatch"
    
    // For getting game set by ID (queryType = "byId")
    private Integer gameSetId;
    
    // For getting game sets by match ID (queryType = "byMatch")
    private Integer matchId;
    
    // Pagination parameters (optional for future use)
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "gameSetNo";
    private String sortDirection = "asc";
}
