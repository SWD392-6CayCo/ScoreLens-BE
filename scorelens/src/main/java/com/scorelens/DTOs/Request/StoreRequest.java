package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequest {
    private String name;
    private String address;
    private String status;
    private String description;
}
