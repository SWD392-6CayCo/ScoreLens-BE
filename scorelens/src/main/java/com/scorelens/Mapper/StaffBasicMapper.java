package com.scorelens.Mapper;

import com.scorelens.DTOs.Response.StaffBasicResponse;
import com.scorelens.Entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffBasicMapper {
    
    @Mapping(source = "staffID", target = "staffID")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "dob", target = "dob")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "status", target = "status")
    StaffBasicResponse toStaffBasicResponse(Staff staff);
}
