package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    @Mapping(source = "manager", target = "manager")
    StaffResponseDto toDto(Staff staff);
    List<StaffResponseDto> toDto(List<Staff> staffList);

    @Mapping(target = "manager", ignore = true)
    Staff toEntity(StaffCreateRequestDto staffRequestDto);
}
