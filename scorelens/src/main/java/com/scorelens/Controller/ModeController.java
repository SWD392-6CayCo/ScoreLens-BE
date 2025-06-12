package com.scorelens.Controller;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.ModeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "Mode", description = "Manage Game Mode")
@RestController
@RequestMapping("v1/modes")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeController {

    @Autowired
    private ModeService modeService;

    @GetMapping("/{id}")
    public ResponseObject getById(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Mode information successfully")
                .data(modeService.getById(id))
                .build();
    }


    @PostMapping
    public ResponseObject createMode(@RequestBody ModeRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Create new Mode successfully")
                .data(modeService.createMode(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateMode(@PathVariable Integer id, @RequestBody ModeRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update Mode information successfully")
                .data(modeService.updateMode(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject delete(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Mode with ID " + id + " has been deleted")
                .data(modeService.delete(id))
                .build();
    }
}
