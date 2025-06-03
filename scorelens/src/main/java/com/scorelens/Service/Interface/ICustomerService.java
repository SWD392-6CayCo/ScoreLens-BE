package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;

import java.util.List;

public interface ICustomerService {
    List<CustomerResponseDto> findAll();
    CustomerResponseDto findById(String id);
    boolean deleteById(String id);
    CustomerResponseDto updateCustomer(String id, CustomerRequestDto requestDto);
    CustomerResponseDto createCustomer(CustomerRequestDto request);
    boolean updateCustomerStatus(String id, String status);
}
