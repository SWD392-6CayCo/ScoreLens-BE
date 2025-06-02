package com.scorelens.Controller;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Tag(name = "Customer", description = "Quản lý mấy khứa khách hàng")
@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    @Autowired
    CustomerService customerService;

    //    ---------------------------------------- GET ----------------------------------------
    @GetMapping("/all")
    public ResponseEntity<ResponseObject> getAllCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(new ResponseObject(200, "Get all customers successfully", customers));
    }

    @GetMapping("/{id}")
    public ResponseObject getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isPresent()) {
            return ResponseObject.builder().status(1000).message("Customer found").data(customer).build();
        }
        return ResponseObject.builder().status(404).message("Customer not found").data(null).build();
    }
    //    ---------------------------------------------------------------------------------------------

    //    ---------------------------------------- CREATE/POST ----------------------------------------
    @PostMapping
    public ResponseObject createCustomer(@RequestBody @Valid CustomerRequestDto requestDto) {
        Customer newCustomer = customerService.createCustomer(requestDto);
        return ResponseObject.builder()
                .status(1000)
                .data(newCustomer)
                .message("Customer created successfully")
                .build();
    }
    //    ---------------------------------------------------------------------------------------------

    //    ---------------------------------------- UPDATE/PUT ----------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.update(id, customer);
        if (updatedCustomer == null) {
            return ResponseEntity.status(404).body(new ResponseObject(404, "Customer not found", null));
        }
        return ResponseEntity.ok(new ResponseObject(200, "Customer updated", updatedCustomer));
    }
    //    -----------------------------------------------------------------------------------------------

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
}
