package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3V3Request {
    // For specifying the S3 query type
    private String queryType; // "listAll", "getFileUrl"
    
    // For getting file URL by filename (queryType = "getFileUrl")
    private String fileName;
    
    // Pagination parameters (optional for future use)
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "fileName";
    private String sortDirection = "asc";
}
