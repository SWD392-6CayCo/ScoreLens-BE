package com.scorelens.DTOs.Response;

import com.scorelens.Enums.TableStatus;
import com.scorelens.Enums.TableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BilliardTableResponse {
    private String billardTableID;
    private String tableCode;
    private TableType tableType;
    private String name;
    private String description;
    private TableStatus status;
    private boolean isActive;
//    private StoreResponse storeResponse;
    private String storeName;
}
