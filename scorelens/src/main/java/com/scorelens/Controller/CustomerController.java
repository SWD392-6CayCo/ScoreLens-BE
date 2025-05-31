package com.scorelens.Controller;

import com.scorelens.Entity.Customer;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@Slf4j
//@RestController
//@RequestMapping("/customer")
//@CrossOrigin(origins = "http://localhost:5173")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class CustomerController {
//
//    CustomerService _customerService;
//
//    @GetMapping("/api/getAllCustomers")
//    public List<Customer> getCustomers() {
//        return _customerService.findAll();
//    }
//
//    @GetMapping("/api/hello")
//    public String hello() {
//        return "Hello World";
//    }
//
//}
@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerService _customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this._customerService = customerService;
    }

    @GetMapping("/api/getAllCustomers")
    public ResponseEntity<ResponseObject> getCustomers() {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(1000)
                        .message("Get All Actice Account Successfully !!")
                        .data(_customerService.findAll())
                        .build()
        );
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello World";
    }
}

