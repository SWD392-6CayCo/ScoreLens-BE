package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Request.CustomerUpdateRequestDto;
import com.scorelens.DTOs.Request.PageableRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.DTOs.Response.PageableResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Customer", description = "Quản lý mấy khứa khách hàng")
@RestController
@RequestMapping("v3/customers")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerV3Controller {

    @Autowired
    CustomerService customerService;

    //    ---------------------------------------- GET ----------------------------------------
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

    @GetMapping("/{id}")
    public ResponseObject getCustomerById(@PathVariable String id) {
        CustomerResponseDto responseDto = customerService.findById(id);
        return ResponseObject.builder()
                .status(1000)
                .message("Customer found")
                .data(responseDto).build();
    }

    @GetMapping("/my-profile") //API lấy tt customer đang login
    public ResponseObject getProfile() {
        CustomerResponseDto responseDto = customerService.getMyProfile();
        return ResponseObject.builder()
                .status(1000)
                .message("Customer found")
                .data(responseDto)
                .build();
    }
    //    ---------------------------------------------------------------------------------------------

    //    ---------------------------------------- CREATE/POST ----------------------------------------
    @PostMapping
    public ResponseObject createCustomer(@RequestBody @Valid CustomerCreateRequestDto requestDto) {
        CustomerResponseDto newCustomer = customerService.createCustomer(requestDto);
        return ResponseObject.builder()
                .status(1000)
                .data(newCustomer)
                .message("Customer created successfully")
                .build();
    }
    //    ---------------------------------------------------------------------------------------------

    //    ---------------------------------------- UPDATE/PUT ----------------------------------------
    @PutMapping("/{id}")
    public ResponseObject updateCustomer(@PathVariable String id, @RequestBody @Valid CustomerUpdateRequestDto requestDto) {
        CustomerResponseDto updatedCustomer = customerService.updateCustomer(id, requestDto);
        if (updatedCustomer == null) {
            return ResponseObject.builder()
                    .status(404)
                    .data(null)
                    .message("Customer not found")
                    .build();
        }
        return ResponseObject.builder()
                .status(1000)
                .data(updatedCustomer)
                .message("Customer updated successfully")
                .build();
    }
    //    -----------------------------------------------------------------------------------------------

    //    ---------------------------------------- UPDATE STATUS (BAN/UNBAN) ----------------------------------------
    @PutMapping("/status/{id}")
    public ResponseObject updateCustomerStatus(@PathVariable String id, @RequestParam String status) {
        boolean updated = customerService.updateCustomerStatus(id, status);
        if(updated) {
            return ResponseObject.builder()
                    .status(1000)
                    .data(null)
                    .message(String.format("Customer status changed to '%s' successfully", status))
                    .build();
        }
        return ResponseObject.builder()
                .status(404)
                .data(null)
                .message("Customer status updated failed")
                .build();
    }
    //    ---------------------------------------------------------------------------------------------

    //    ---------------------------------------- DELETE ------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCustomer(@PathVariable String id) {
        boolean deleted = customerService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok(new ResponseObject(200, "Customer deleted", null));
        }
        return ResponseEntity.status(404).body(new ResponseObject(404, "Customer not found", null));
    }
    //    ---------------------------------------------------------------------------------------------------

    //    ---------------------------------------- UPDATE PASSWORD ------------------------------------------------
    @PutMapping("/password/{id}")
    public ResponseObject updatePassword(@PathVariable String id, @RequestBody @Valid ChangePasswordRequestDto requestDto) {
        boolean updated = customerService.updatePassword(id, requestDto);
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
