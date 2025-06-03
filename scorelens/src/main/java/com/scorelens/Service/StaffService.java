package com.scorelens.Service;

import com.scorelens.DTOs.Request.StaffRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.IDSequence;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.StatusType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.StaffMapper;
import com.scorelens.Repository.IDSequenceRepository;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Service.Interface.IStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
//đây là SpringFramwork, Không nhầm với jakarta.transaction.Transactional,
// cái đó là JTA (Java EE/Jakarta EE),
// còn Spring dùng của chính nó để dễ kiểm soát và tích hợp với Spring Context.

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService implements IStaffService {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    IDSequenceRepository idSequenceRepository;

    @Autowired
    private StaffMapper staffMapper;

    //    ---------------------------- GET BY ID -----------------------------------
    public StaffResponseDto getStaffById(String id) {
        Optional<Staff> optionalStaff = staffRepository.findById(id);
        if (optionalStaff.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        Staff staff = optionalStaff.get();

        return staffMapper.toDto(staff);
    }
    //    --------------------------------------------------------------------------

    //    ---------------------------- GET ALL -----------------------------------
    public List<StaffResponseDto> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        if (staffList.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_LIST);
        }
        return staffMapper.toDto(staffList);
    }
    //    --------------------------------------------------------------------------

    //    ---------------------------- CREATE STAFF-----------------------------------
    @Transactional
    @Override
    public StaffResponseDto createStaff(StaffRequestDto staffRequestDto) {
        StaffRole role = staffRequestDto.getRole();

        String prefix = switch (role) {
            case Staff -> "S";
            case Manager -> "M";
            case Admin -> "A";
            default -> throw new IllegalArgumentException("Invalid staff role");
        };

        // Lock row and increment
        IDSequence sequence = idSequenceRepository.findAndLockByRolePrefix(prefix);
        Long nextNumber = sequence.getLastNumber() + 1;
        sequence.setLastNumber(nextNumber);
        idSequenceRepository.save(sequence);

        // Generate staffID
        String staffID = String.format("%s%07d", prefix, nextNumber);

        //Kiểm tra xem Email và PhoneNumber đã đc sử dụng hay chưa-------
        if(staffRepository.existsByEmail(staffRequestDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if(staffRepository.existsByPhoneNumber(staffRequestDto.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        //----------------------------------------------------------------

        Staff staff = staffMapper.toEntity(staffRequestDto);
        staff.setStaffID(staffID);
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);

        return staffMapper.toDto(staffRepository.save(staff));
    }
    //    --------------------------------------------------------------------------



}
