package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamV3Request {
    // For specifying query type
    private String queryType; // "byId", "byMatch"
    
    // For getting team by ID (queryType = "byId")
    private Integer teamId;
    
    // For getting teams by match ID (queryType = "byMatch")
    private Integer matchId;
    
    // Pagination parameters (optional for future use)
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "createAt";
    private String sortDirection = "desc";
}
