package com.scorelens.Service;

import com.scorelens.DTOs.Request.StaffRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.Customer;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Override
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
    @Override
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

        //upload ảnh...

        //Dùng BCrypt để mã hóa mật khẩu khi lưu vào DB
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        staff.setPassword(passwordEncoder.encode(staffRequestDto.getPassword()));
        staffRepository.save(staff);

        return staffMapper.toDto(staffRepository.save(staff));
    }


    //    ---------------------------- UPDATE STAFF-----------------------------------
    @Override
    public StaffResponseDto updateStaff(String id, StaffRequestDto requestDto) {
        // Tìm nhân viên theo ID
        Staff existingStaff = staffRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        // Kiểm tra Email đã được dùng bởi người khác chưa
        if (staffRepository.existsByEmail(requestDto.getEmail()) &&
                !existingStaff.getEmail().equals(requestDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }

        // Kiểm tra số điện thoại đã được dùng bởi người khác chưa
        if (staffRepository.existsByPhoneNumber(requestDto.getPhoneNumber()) &&
                !existingStaff.getPhoneNumber().equals(requestDto.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        // Cập nhật thông tin
        existingStaff.setName(requestDto.getName());
        existingStaff.setEmail(requestDto.getEmail());
        existingStaff.setPhoneNumber(requestDto.getPhoneNumber());
        existingStaff.setRole(requestDto.getRole());
        existingStaff.setAddress(requestDto.getAddress());
        existingStaff.setStatus(requestDto.getStatus());
        existingStaff.setDob(requestDto.getDob());
        existingStaff.setUpdateAt(LocalDate.now());

        // Nếu password mới được cung cấp, mã hóa lại
        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            existingStaff.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        // Lưu và trả về kết quả
        staffRepository.save(existingStaff);
        return staffMapper.toDto(existingStaff);
    }

    //    ---------------------------- DELETE STAFF-----------------------------------
    @Override
    public boolean deleteStaff(String id) {
        if(staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //    ---------------------------- BAN/UNBANED STAFF-----------------------------------
    @Override
    public boolean updateStaffStatus(String id, String status) {
        boolean check = true;
        Staff c = staffRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));
        if (!status.equalsIgnoreCase(StatusType.active.toString()) && !status.equalsIgnoreCase(StatusType.inactive.toString())) {
            throw new AppException(ErrorCode.INVALID_STATUS); // Optional: bạn có thể thêm enum hoặc custom error code
        }
        c.setStatus(StatusType.valueOf(status));
        c.setUpdateAt(LocalDate.now());
        staffRepository.save(c);

        return check;
    }
    //    --------------------------------------------------------------------------




}
