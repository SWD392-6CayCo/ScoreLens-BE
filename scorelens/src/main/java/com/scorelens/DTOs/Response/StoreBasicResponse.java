package com.scorelens.DTOs.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * DTO for basic Store information (without relationships)
 */
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreBasicResponse implements Serializable {
    String storeID;
    String name;
    String address;
    String status;
    String description;
}
