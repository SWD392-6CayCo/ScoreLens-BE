package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    List<CustomerResponseDto> toDtoList(List<Customer> customerList);
    CustomerResponseDto toDto(Customer customer);
    Customer toEntity(CustomerRequestDto customerRequestDto);
}
