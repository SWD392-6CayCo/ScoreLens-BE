package com.scorelens.DTOs.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreResponse {
    private String storeID;
    private String name;
    private String address;
    private String status;
    private String description;
    private List<BilliardTableResponse> billiardTables;

}
