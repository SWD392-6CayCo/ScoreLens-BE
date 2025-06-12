package com.scorelens.Controller;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Billiard Table", description = "Manage Billiard Table")
@RestController
@RequestMapping("/tables")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardTableController {

    BilliardTableService billiardTableService;

    StoreService storeService;

    @PostMapping
    public ResponseObject addTable(@RequestBody BilliardTableRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("New table is created")
                .data(billiardTableService.createBilliardTable(request))
                .build();
    }

    @GetMapping
    public ResponseObject getAllTables() {
        return ResponseObject.builder()
                .status(1000)
                .message("All tables")
                .data(billiardTableService.getAllBilliardTables())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject getTableById(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Table with id " + id)
                .data(billiardTableService.findBilliardTableById(id))
                .build();
    }


    @PutMapping("/{id}")
    public ResponseObject updateTable(@PathVariable String id, @RequestBody BilliardTableRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Table is updated")
                .data(billiardTableService.updateBilliardTable(id, request))
                .build();
    }

    @PutMapping("/status/{id}")
    public ResponseObject updateTableStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseObject.builder()
                .status(1000)
                .message("Table is updated")
                .data(billiardTableService.updateBilliardTable(id, status))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deleteTable(@PathVariable String id) {
        String tableCode = billiardTableService.findBilliardTableById(id).getTableCode();
        log.info("Table {} is deleted", tableCode);
        return ResponseObject.builder()
                .status(1000)
                .message(String.format("Table with code %s is deleted", tableCode))
                .data(billiardTableService.deleteBilliardTable(id))
                .build();

    }

    @GetMapping("/v2/{id}")
    public ResponseObject getListByStoreID(@PathVariable String id){
        StoreResponse store = storeService.findStoreById(id);
        return ResponseObject.builder()
                .status(1000)
                .message("Tables in Store name:" + store.getName())
                .data(billiardTableService.getTablesByStore(id))
                .build();
    }

}



















