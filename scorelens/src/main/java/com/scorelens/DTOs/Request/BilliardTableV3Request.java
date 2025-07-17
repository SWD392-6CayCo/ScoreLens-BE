package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardTableV3Request {
    // For specifying query type
    private String queryType; // "all", "byId", "byStore"
    
    // For getting table by ID (queryType = "byId")
    private String tableId;
    
    // For getting tables by store ID (queryType = "byStore")
    private String storeId;
    
    // Pagination parameters (optional for future use)
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "tableCode";
    private String sortDirection = "asc";
}
