package com.scorelens.Controller;

import com.scorelens.Entity.Customer;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/all")
    List<Customer> getCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello World";
    }


}
