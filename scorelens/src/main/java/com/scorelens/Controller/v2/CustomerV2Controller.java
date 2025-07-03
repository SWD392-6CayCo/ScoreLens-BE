package com.scorelens.Controller.v2;

import com.scorelens.DTOs.Request.PageableRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.DTOs.Response.PageableResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Customer V2", description = "Customer management with pagination, search and filter")
@RestController
@RequestMapping("v2/customers")
@CrossOrigin(origins = {"http://localhost:5173", "exp://192.168.90.68:8081"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerV2Controller {

    CustomerService customerService;

    @Operation(summary = "Get customers with pagination, search and filter")
    @GetMapping
    public ResponseObject getCustomers(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "Search keyword (name, email, phone)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status (active/inactive)") @RequestParam(required = false) String status
    ) {
        PageableRequestDto request = PageableRequestDto.builder()
                .page(page - 1) // Convert 1-based to 0-based for Spring Data
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .search(search)
                .status(status)
                .build();

        PageableResponseDto<CustomerResponseDto> response = customerService.getCustomersWithPagination(request);
        
        return ResponseObject.builder()
                .status(1000)
                .message("Get customers successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public ResponseObject getCustomerById(@PathVariable String id) {
        CustomerResponseDto customer = customerService.findById(id);
        return ResponseObject.builder()
                .status(1000)
                .message("Customer found")
                .data(customer)
                .build();
    }
}
