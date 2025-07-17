package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventV3Request {
    // For specifying query type
    private String queryType; // "all", "byPlayer", "byGameSet", "byPlayerAndGameSet", "countByGameSet"
    
    // For getting events by player ID (queryType = "byPlayer")
    private Integer playerId;
    
    // For getting events by game set ID (queryType = "byGameSet" or "countByGameSet")
    private Integer gameSetId;
    
    // Pagination parameters (optional for future use)
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "timeStamp";
    private String sortDirection = "desc";
}
