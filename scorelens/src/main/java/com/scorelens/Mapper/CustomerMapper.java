package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Request.CustomerUpdateRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    List<CustomerResponseDto> toDtoList(List<Customer> customerList);
    CustomerResponseDto toDto(Customer customer);
    Customer toEntity(CustomerCreateRequestDto customerCreateRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Customer customer, CustomerCreateRequestDto customerCreateRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Customer customer, CustomerUpdateRequestDto customerUpdateRequestDto);
}
