package com.scorelens.Controller.v1;

import com.scorelens.DTOs.Request.BilliardMatchCreateRequest;
import com.scorelens.DTOs.Request.BilliardMatchUpdateRequest;
import com.scorelens.DTOs.Request.GameSetCreateRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.BilliardMatchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Billiard Match", description = "Manage Billiard Match")
@RestController
@RequestMapping("v1/billiardmatches")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardMatchV1Controller {

    @Autowired
    BilliardMatchService billiardMatchService;

    @GetMapping("/{id}")
    public ResponseObject getById(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Get Match information successfully")
                        .data(billiardMatchService.getById(id))
                        .build();
    }

    @GetMapping("/bycreator/customer/{id}")
    public ResponseObject getByCustomer(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getByCustomer(id))
                .build();
    }

    @GetMapping("/bycreator/staff/{id}")
    public ResponseObject getByStaff(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getByStaff(id))
                .build();
    }

    @GetMapping("/bycustomer/{id}")
    public ResponseObject getByCustomerID(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getByCustomerID(id))
                .build();
    }

    @GetMapping("/byplayer/{id}")
    public ResponseObject getByPlayerID(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Match information successfully")
                .data(billiardMatchService.getByPlayerID(id))
                .build();
    }

    @PostMapping
    public ResponseObject createMatch(@RequestBody BilliardMatchCreateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Create new Match successfully")
                .data(billiardMatchService.createMatch(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateMatch(@PathVariable Integer id, @RequestBody BilliardMatchUpdateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update Match information successfully")
                .data(billiardMatchService.updateMatch(id,request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deleteMatch(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Match with ID " + id + " has been deleted")
                        .data(billiardMatchService.delete(id))
                        .build();
    }
}

