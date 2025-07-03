package com.scorelens.DTOs.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableResponseDto<T> {
    List<T> content; // List of items
    int page; // Current page number
    int size; // Page size
    long totalElements; // Total number of elements
    int totalPages; // Total number of pages
    boolean first; // Is first page
    boolean last; // Is last page
    boolean empty; // Is empty page
    String sortBy; // Sort field
    String sortDirection; // Sort direction
}
