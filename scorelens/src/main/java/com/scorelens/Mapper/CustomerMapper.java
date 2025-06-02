package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.Entity.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequestDto customerRequestDto);
    CustomerRequestDto toDto(Customer customer);
    List<CustomerRequestDto> toDtoList(List<Customer> customers);
}
