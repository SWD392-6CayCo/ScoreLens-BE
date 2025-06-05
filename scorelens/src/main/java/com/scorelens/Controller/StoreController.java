package com.scorelens.Controller;


import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Store", description = "Manage Store")
@RestController
@RequestMapping("/stores")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreController {

    @Autowired
    StoreService storeService;

    @GetMapping
    public ResponseObject allStores() {
        return ResponseObject.builder()
                .status(1000)
                .message("All Stores")
                .data(storeService.findAllStores())
                .build();
    }

    @PostMapping
    public ResponseObject addStore(@RequestBody StoreRequest storeRequest) {
        return ResponseObject.builder()
                .status(1000)
                .message("New Store is created")
                .data(storeService.createStore(storeRequest))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject getStoreById(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Store found")
                .data(storeService.findStoreById(id))
                .build();
    }

}
