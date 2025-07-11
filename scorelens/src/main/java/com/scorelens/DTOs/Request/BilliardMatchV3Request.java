package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class BilliardMatchV3Request {
    // For filtering matches
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;
    private String status;
    private Integer modeID;
    
    // For getting matches by specific IDs
    private Integer matchId;
    private String customerId;
    private String staffId;
    private Integer playerId;
    
    // For specifying query type
    private String queryType; // "byId", "byCustomer", "byStaff", "byPlayer", "byCreatorCustomer", "byCreatorStaff", "filter"
    
    // Pagination parameters
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "startTime";
    private String sortDirection = "desc";
}
