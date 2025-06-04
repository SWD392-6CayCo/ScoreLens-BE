package com.scorelens.Controller;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StaffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Staff", description = "Quản lý Admin, Manager và Staff")
@RestController
@RequestMapping("/staffs")
public class StaffController {

    @Autowired
    StaffService staffService;

    //    ---------------------------------------- GET ----------------------------------------
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

    //    ---------------------------------------- CREATE/POST ----------------------------------------
    @PostMapping
    public ResponseObject addStaff(@RequestBody @Valid StaffCreateRequestDto requestDto) {
        StaffResponseDto staff = staffService.createStaff(requestDto);
        return ResponseObject.builder()
                .status(1000)
                .data(staff)
                .message("Staff created successfully.")
                .build();
    }

    //    ---------------------------------------- UPDATE/PUT ----------------------------------------
    @PutMapping("/{id}")
    public ResponseObject updateStaff(@PathVariable String id, @RequestBody @Valid StaffUpdateRequestDto requestDto) {
        StaffResponseDto updated = staffService.updateStaff(id, requestDto);
        return ResponseObject.builder()
                .status(1000)
                .message("Staff updated successfully.")
                .data(updated)
                .build();
    }

    //    ---------------------------------------- DELETE ------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteStaff(@PathVariable String id) {
        boolean deleted = staffService.deleteStaff(id);
        if (deleted) {
            return ResponseEntity.ok(new ResponseObject(200, "Staff deleted", null));
        }
        return ResponseEntity.status(404).body(new ResponseObject(404, "Staff not found", null));
    }
    //    ---------------------------------------------------------------------------------------------------

    //    ---------------------------------------- UPDATE STATUS (BAN/UNBAN) ----------------------------------------
    @PutMapping("/{id}/status")
    public ResponseObject updateStaffStatus(@PathVariable String id, @RequestParam String status) {
        boolean updated = staffService.updateStaffStatus(id, status);
        if(updated) {
            return ResponseObject.builder()
                    .status(1000)
                    .data(null)
                    .message(String.format("Staff status changed to '%s' successfully", status))
                    .build();
        }
        return ResponseObject.builder()
                .status(404)
                .data(null)
                .message("Staff status updated failed")
                .build();
    }
    //    ---------------------------------------------------------------------------------------------

    //    ---------------------------------------- UPDATE PASSWORD ------------------------------------------------
    @PutMapping("/{id}/password")
    public ResponseObject updatePassword(@PathVariable String id, @RequestBody @Valid ChangePasswordRequestDto requestDto) {
        boolean updated = staffService.updatePassword(id, requestDto);
        if (updated) {
            return ResponseObject.builder()
                    .status(1000)
                    .data(null)
                    .message("Password updated successfully")
                    .build();
        }
        return ResponseObject.builder()
                .status(404)
                .data(null)
                .message("Password updated failed")
                .build();
    }
}
