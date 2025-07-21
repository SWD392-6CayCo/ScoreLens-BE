package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.PageableRequestDto;
import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.DTOs.Response.PageableResponseDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Staff", description = "Quản lý Admin, Manager và Staff")
@RestController
@RequestMapping("v3/staffs")
public class StaffV3Controller {

    @Autowired
    StaffService staffService;

    //    ---------------------------------------- GET ----------------------------------------
    @PreAuthorize("hasAuthority('GET_STAFF_LIST')")
    @Operation(summary = "Get staffs with pagination, search and filter")
    @GetMapping
    public ResponseObject getStaffs(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "Search keyword (name, email, phone)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status (active/inactive)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by store ID") @RequestParam(required = false) String storeId,
            @Parameter(description = "Filter by role") @RequestParam(required = false) String role
    ) {
        PageableRequestDto request = PageableRequestDto.builder()
                .page(page - 1) // Convert 1-based to 0-based for Spring Data
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .search(search)
                .status(status)
                .build();

        PageableResponseDto<StaffResponseDto> response = staffService.getStaffsWithPagination(request, storeId, role);

        return ResponseObject.builder()
                .status(1000)
                .message("Get staffs successfully")
                .data(response)
                .build();
    }
    @GetMapping("/{id}")
    public ResponseObject getStaffById(@PathVariable String id) {
        StaffResponseDto staff = staffService.getStaffById(id);
        return ResponseObject.builder()
                .status(1000)
                .data(staff)
                .message("Staff found")
                .build();
    }

    @GetMapping("/my-profile") //API lấy tt customer đang login
    public ResponseObject getProfile() {
        StaffResponseDto responseDto = staffService.getMyProfile();
        return ResponseObject.builder()
                .status(1000)
                .message("Staff found")
                .data(responseDto)
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
    @PutMapping("/status/{id}")
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
    @PutMapping("/password/{id}")
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
