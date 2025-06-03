package com.scorelens.Controller;

import com.scorelens.DTOs.Request.StaffRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StaffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Staff", description = "Quản lý Admin, Manager và Staff")
@RestController
@RequestMapping("/staffs")
public class StaffController {

    @Autowired
    StaffService staffService;

    @GetMapping("/all")
    public ResponseObject getAllStaff() {
        List<StaffResponseDto> staffs = staffService.getAllStaff();
        return ResponseObject.builder()
                .status(1000)
                .data(staffs)
                .message("Get all staffs successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject getStaffById(@PathVariable String id) {
        StaffResponseDto staff = staffService.getStaffById(id);
        return ResponseObject.builder()
                .status(1000)
                .data(staff)
                .message("Staff founded")
                .build();
    }

    @PostMapping
    public ResponseObject addStaff(@RequestBody StaffRequestDto requestDto) {
        StaffResponseDto staff = staffService.createStaff(requestDto);
        return ResponseObject.builder()
                .status(1000)
                .data(staff)
                .message("Staff created successfully.")
                .build();
    }
}
