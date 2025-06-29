package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scorelens.Enums.TableStatus;
import com.scorelens.Enums.TableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BilliardTableResponse {
    private String billardTableID;
    private String tableCode;
    private TableType tableType;
    private String name;
    private String description;
    private TableStatus status;
    private String qrCode;
    private String cameraUrl;
    private boolean isActive;
    private String storeID;
    private BilliardMatchResponse matchResponse;
}
