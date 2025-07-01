package com.scorelens.Controller.v2;

import com.scorelens.DTOs.Request.PageableRequestDto;
import com.scorelens.DTOs.Response.PageableResponseDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Staff V2", description = "Staff management with pagination, search and filter")
@RestController
@RequestMapping("v2/staffs")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffV2Controller {

    StaffService staffService;

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

    @Operation(summary = "Get staff by ID")
    @GetMapping("/{id}")
    public ResponseObject getStaffById(@PathVariable String id) {
        StaffResponseDto staff = staffService.getStaffById(id);
        return ResponseObject.builder()
                .status(1000)
                .message("Staff found")
                .data(staff)
                .build();
    }
}
