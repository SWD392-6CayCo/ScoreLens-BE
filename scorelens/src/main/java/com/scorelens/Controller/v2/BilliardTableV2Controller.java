package com.scorelens.Controller.v2;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.MatchStatus;
import com.scorelens.Enums.TableStatus;
import com.scorelens.Service.BilliardMatchService;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Billiard Table", description = "Manage Billiard Table")
@RestController
@RequestMapping("v2/tables")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardTableV2Controller {

    BilliardTableService billiardTableService;

    StoreService storeService;

    BilliardMatchService billiardMatchService;

    @GetMapping("/{id}")
    public ResponseObject getListByStoreID(@PathVariable String id) {
        StoreResponse store = storeService.findStoreById(id);
        List<BilliardTableResponse> response = billiardTableService.getTablesByStore(id);
        //add match to table when table is in use
        for (BilliardTableResponse table : response) {
            if (table.getStatus().equals(TableStatus.inUse)) {
                BilliardMatchResponse tmp =
                        billiardMatchService.getOnGoingMatch(table.getBillardTableID());
                if (tmp != null) {
                    table.setMatchResponse(tmp);
                }
            }
        }
        return ResponseObject.builder()
                .status(1000)
                .message("Tables in Store name:" + store.getName())
                .data(response)
                .build();
    }
}



















