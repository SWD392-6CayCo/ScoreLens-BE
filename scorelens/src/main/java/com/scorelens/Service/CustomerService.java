package com.scorelens.Service;

import com.scorelens.Entity.Customer;
import com.scorelens.Repository.CustomerRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerService {

    @Autowired
    CustomerRepo customerRepo;

    public List<Customer> findAll() {return customerRepo.findAll();}


}
