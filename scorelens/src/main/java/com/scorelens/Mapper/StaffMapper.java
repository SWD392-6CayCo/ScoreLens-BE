package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.StaffRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    StaffResponseDto toDto(Staff staff);
    List<StaffResponseDto> toDto(List<Staff> staffList);
    Staff toEntity(StaffRequestDto staffRequestDto);
}
