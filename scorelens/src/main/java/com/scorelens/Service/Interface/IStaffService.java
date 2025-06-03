package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.StaffRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;

import java.util.List;

public interface IStaffService {
    StaffResponseDto getStaffById(String id);
    List<StaffResponseDto> getAllStaff();
    StaffResponseDto createStaff(StaffRequestDto staffRequestDto);
    StaffResponseDto updateStaff(String id, StaffRequestDto staffRequestDto);
    boolean deleteStaff(String id);
    boolean updateStaffStatus(String id, String status);
}
