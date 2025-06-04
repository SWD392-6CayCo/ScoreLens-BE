package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;

import java.util.List;

public interface IStaffService {
    StaffResponseDto getStaffById(String id);
    List<StaffResponseDto> getAllStaff();
    StaffResponseDto createStaff(StaffCreateRequestDto staffCreateRequestDto);
    StaffResponseDto updateStaff(String id, StaffUpdateRequestDto staffUpdateRequestDto);
    boolean deleteStaff(String id);
    boolean updateStaffStatus(String id, String status);
    boolean updatePassword (String id, ChangePasswordRequestDto requestDto);
}
