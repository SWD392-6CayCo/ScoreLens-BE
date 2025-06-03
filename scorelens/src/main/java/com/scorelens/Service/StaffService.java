package com.scorelens.Service;

import com.scorelens.DTOs.Request.StaffRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StatusType;
import com.scorelens.Mapper.StaffMapper;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Service.Interface.IStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService implements IStaffService {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffMapper staffMapper;

    //    ---------------------------- GET BY ID -----------------------------------
    public StaffResponseDto getStaffById(String id) {
        Optional<Staff> optionalStaff = staffRepository.findById(id);
        if (optionalStaff.isEmpty()) {
            return null;
        }
        Staff staff = optionalStaff.get();

        return staffMapper.toDto(staff);
    }
    //    --------------------------------------------------------------------------

    //    ---------------------------- GET ALL -----------------------------------
    public List<StaffResponseDto> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        return staffMapper.toDto(staffList);
    }
    //    --------------------------------------------------------------------------

    //    ---------------------------- GET ALL -----------------------------------
    @Override
    public StaffResponseDto createStaff(StaffRequestDto staffRequestDto) {

        Staff staff = staffMapper.toEntity(staffRequestDto);
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);
        StaffResponseDto staffResponseDto = staffMapper.toDto(staffRepository.save(staff));
        return staffResponseDto;
    }
    //    --------------------------------------------------------------------------



}
