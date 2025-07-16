package com.scorelens.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDataResponse {
    private int totalTables;
    private int currentlyPlaying;
    private int availableTables;
    private int brokenTables;
    private List<CustomerMatchResponse> customers;
}

