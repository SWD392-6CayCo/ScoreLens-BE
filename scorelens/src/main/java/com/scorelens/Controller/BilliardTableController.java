package com.scorelens.Controller;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.BilliardTableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Billiard Table", description = "Manage Billiard Table")
@RestController
@RequestMapping("/tables")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardTableController {

    @Autowired
    BilliardTableService billiardTableService;

    @PostMapping
    public ResponseObject addTable(@RequestBody BilliardTableRequest request){
        return ResponseObject.builder()
                .status(1000)
                .message("New table is created")
                .data(billiardTableService.createBilliardTable(request))
                .build();
    }


















}
