package com.scorelens.DTOs.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableRequestDto {
    @Builder.Default
    int page = 0; // Page number (0-based)
    
    @Builder.Default
    int size = 10; // Page size
    
    @Builder.Default
    String sortBy = "createAt"; // Sort field
    
    @Builder.Default
    String sortDirection = "desc"; // Sort direction: asc, desc
    
    String search; // Search keyword
    String status; // Filter by status: active, inactive
}
