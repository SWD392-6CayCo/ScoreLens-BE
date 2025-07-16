package com.scorelens.Controller.v1;


import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Store", description = "Manage Store")
@RestController
@RequestMapping("v1/stores")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreV1Controller {

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

    @GetMapping("data/{id}")
    public ResponseObject getStoreData(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Store found")
                .data(storeService.getStoreData(id))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateStore(@PathVariable String id, @RequestBody StoreRequest storeRequest) {
        return ResponseObject.builder()
                .status(1000)
                .message("Store is updated")
                .data(storeService.updateStore(id, storeRequest))
                .build();
    }

    @PutMapping("/status/{id}")
    public ResponseObject updateStoreStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseObject.builder()
                .status(1000)
                .message("Store's status is updated")
                .data(storeService.updateStore(id, status))
                .build();
    }



}
