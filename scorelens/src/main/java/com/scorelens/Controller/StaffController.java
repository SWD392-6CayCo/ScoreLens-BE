package com.scorelens.Controller;

import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StaffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
