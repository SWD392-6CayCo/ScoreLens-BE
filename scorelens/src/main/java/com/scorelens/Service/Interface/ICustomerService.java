package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Request.CustomerUpdateRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;

import java.util.List;

public interface ICustomerService {
    List<CustomerResponseDto> findAll();
    CustomerResponseDto findById(String id);
    CustomerResponseDto getMyProfile();
    boolean deleteById(String id);
    CustomerResponseDto updateCustomer(String id, CustomerUpdateRequestDto requestDto);
    CustomerResponseDto createCustomer(CustomerCreateRequestDto request);
    boolean updateCustomerStatus(String id, String status);
    boolean updatePassword (String id, ChangePasswordRequestDto requestDto);
}
