package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequestDto customerRequestDto);

    void toEntity(@MappingTarget Customer customer, CustomerRequestDto customerRequestDto);

    CustomerRequestDto toRequestDto(Customer customer);
    CustomerResponseDto toResponseDto(Customer customer);

    List<CustomerRequestDto> toRequestDtoList(List<Customer> customers);
    List<CustomerResponseDto> toResponseDtoList(List<Customer> customers);
}
