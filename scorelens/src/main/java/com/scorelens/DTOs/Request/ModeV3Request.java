package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModeV3Request {
    // For specifying query type
    private String queryType; // "all", "byId"
    
    // For getting mode by ID (queryType = "byId")
    private Integer modeId;
    
    // Pagination parameters (optional for future use)
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "name";
    private String sortDirection = "asc";
}
