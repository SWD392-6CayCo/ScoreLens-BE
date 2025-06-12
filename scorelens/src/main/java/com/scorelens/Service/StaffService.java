package com.scorelens.Service;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserValidatorService userValidatorService;

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

    @Override
    public StaffResponseDto getMyProfile() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName(); //authentication.name là email
        Staff s = staffRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        return staffMapper.toDto(s);
    }
    //    --------------------------------------------------------------------------


    //    ---------------------------- CREATE STAFF-----------------------------------
    @Transactional
    @Override
    public StaffResponseDto createStaff(StaffCreateRequestDto staffCreateRequestDto) {
        StaffRole role = staffCreateRequestDto.getRole();

        String prefix = switch (role) {
            case STAFF -> "S";
            case MANAGER -> "M";
            case ADMIN -> "A";
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
        userValidatorService.validateEmailAndPhoneUnique(staffCreateRequestDto.getEmail(), staffCreateRequestDto.getPhoneNumber());
        //----------------------------------------------------------------

        Staff staff = staffMapper.toEntity(staffCreateRequestDto);
        staff.setStaffID(staffID);
        staff.setCreateAt(LocalDate.now());
        staff.setStatus(StatusType.active);

        if(staffCreateRequestDto.getManagerID() != null) {
            Staff manager = staffRepository.findById(staffCreateRequestDto.getManagerID())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            staff.setManager(manager);
        }

        //upload ảnh...

        //Dùng BCrypt để mã hóa mật khẩu khi lưu vào DB
        staff.setPassword(passwordEncoder.encode(staffCreateRequestDto.getPassword()));
        staffRepository.save(staff);

        return staffMapper.toDto(staffRepository.save(staff));
    }

    //    ---------------------------- UPDATE STAFF-----------------------------------
    @Override
    public StaffResponseDto updateStaff(String id, StaffUpdateRequestDto requestDto) {
        // Tìm nhân viên theo ID
        Staff existingStaff = staffRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        // Kiểm tra Email & Phonenumber đã được dùng bởi người khác chưa
        userValidatorService.validatePhoneUnique(requestDto.getPhoneNumber(), existingStaff.getPhoneNumber());
        userValidatorService.validateEmailUnique(requestDto.getEmail(), existingStaff.getEmail());

        //Kiểm tra xem có managerID hay chưa
        Staff manager = staffRepository.findById(requestDto.getManagerID())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        // Cập nhật thông tin
        existingStaff.setName(requestDto.getName());
        existingStaff.setEmail(requestDto.getEmail());
        existingStaff.setPhoneNumber(requestDto.getPhoneNumber());
//        existingStaff.setRole(requestDto.getRole()); // không cho set role
        existingStaff.setAddress(requestDto.getAddress());
        existingStaff.setStatus(requestDto.getStatus());
        existingStaff.setDob(requestDto.getDob());
        existingStaff.setUpdateAt(LocalDate.now());
        existingStaff.setManager(manager);

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

    //    ---------------------------- UPDATE PASSWORD-----------------------------------
    @Override
    public boolean updatePassword (String id, ChangePasswordRequestDto requestDto){
        boolean check = false;
        Staff s = staffRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));

        String staffPassword = s.getPassword();
        if(passwordEncoder.matches(requestDto.getOldPassword(), staffPassword)){ //password nhập vào đúng với pass của user
            //cho phép đổi
            if(requestDto.getOldPassword().equals(requestDto.getNewPassword())){ //pass mới trùng với pass cũ
                throw new AppException(ErrorCode.DUPLICATED_PASSWORD);
            }
            //pass mới khác pass cũ
            s.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            staffRepository.save(s);
            check = true;
        } else {
            //không cho phép đổi
            System.out.println(requestDto.getOldPassword());
            System.out.println(staffPassword);
            throw new AppException(ErrorCode.NOT_MATCH_PASSWORD);

        }
        return check;
    }
    //    --------------------------------------------------------------------------
}
