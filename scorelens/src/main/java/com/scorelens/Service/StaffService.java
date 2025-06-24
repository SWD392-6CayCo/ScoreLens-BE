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
import com.scorelens.Repository.RoleRepository;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Service.Interface.IStaffService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
//đây là SpringFramwork, Không nhầm với jakarta.transaction.Transactional,
// cái đó là JTA (Java EE/Jakarta EE),
// còn Spring dùng của chính nó để dễ kiểm soát và tích hợp với Spring Context.

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class StaffService implements IStaffService {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    RoleRepository roleRepository;
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
    @PostAuthorize("hasAnyRole('ADMIN', 'MANAGER') or returnObject.email == authentication.name")
    public StaffResponseDto getStaffById(String id) {
        Optional<Staff> optionalStaff = staffRepository.findById(id);
        if (optionalStaff.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        Staff staff = optionalStaff.get();
        var responseDto = staffMapper.toDto(staff);
                String returnObjectName = responseDto.getEmail(); // assuming getName() exists
        String authenticatedName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("returnObject.email = {}, authentication.name = {}", returnObjectName, authenticatedName);

        return responseDto;
    }
    //    --------------------------------------------------------------------------

    //    ---------------------------- GET ALL -----------------------------------

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_STAFF_LIST')")
    public List<StaffResponseDto> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        if (staffList.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_LIST);
        }
        return staffMapper.toDto(staffList);
    }

    @Override
    @PostAuthorize("returnObject.email == authentication.name")
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
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_STAFF')")
    public StaffResponseDto createStaff(StaffCreateRequestDto staffCreateRequestDto) {
        Set<String> roles = staffCreateRequestDto.getRoles();
        String r = roles.stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No role provided"));
        boolean roleExists = roleRepository.existsById(r);
        if (!roleExists) {
            throw new IllegalArgumentException("Role " + r + " does not exist in the system.");
        }
        String prefix = r.substring(0, 2).toUpperCase();

//        // Lock row and increment
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

        if(staffCreateRequestDto.getManagerID() != null && !staffCreateRequestDto.getManagerID().trim().isEmpty()) {
            Staff manager = staffRepository.findById(staffCreateRequestDto.getManagerID())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            staff.setManager(manager);
        }

        //upload ảnh...

        //Dùng BCrypt để mã hóa mật khẩu khi lưu vào DB
        staff.setPassword(passwordEncoder.encode(staffCreateRequestDto.getPassword()));
        var roleList = roleRepository.findAllById(roles);
        staff.setRoles(new HashSet<>(roleList));
        staffRepository.save(staff);

        return staffMapper.toDto(staffRepository.save(staff));
    }

    //    ---------------------------- UPDATE STAFF-----------------------------------
    @Override
    @PostAuthorize("hasAuthority('UPDATE_STAFF_DETAIL') or returnObject.email == authentication.name")
        public StaffResponseDto updateStaff(String id, StaffUpdateRequestDto requestDto) {
        // Tìm nhân viên theo ID
        Staff existingStaff = staffRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.STAFF_NOT_EXIST)
        );

        // Kiểm tra Email & Phonenumber đã được dùng bởi người khác chưa
        userValidatorService.validatePhoneUnique(requestDto.getPhoneNumber(), existingStaff.getPhoneNumber());
        userValidatorService.validateEmailUnique(requestDto.getEmail(), existingStaff.getEmail());

        //Kiểm tra xem có managerID hay chưa
        Staff manager = null;
        if (requestDto.getManagerID() != null && !requestDto.getManagerID().trim().isEmpty()) {
            manager = staffRepository.findById(requestDto.getManagerID())
                    .orElseThrow(() -> new AppException(ErrorCode.MANAGER_NOT_EXIST));
        }

        staffMapper.updateStaff(existingStaff, requestDto);

        // Cập nhật thông tin
        existingStaff.setName(requestDto.getName());
        existingStaff.setEmail(requestDto.getEmail());
        existingStaff.setPhoneNumber(requestDto.getPhoneNumber());

//        existingStaff.setRole(requestDto.getRole()); // không cho set role

        var roles = roleRepository.findAllById(requestDto.getRoles());
        existingStaff.setRoles(new HashSet<>(roles));

        existingStaff.setAddress(requestDto.getAddress());
        existingStaff.setStatus(requestDto.getStatus());
        existingStaff.setDob(requestDto.getDob());
        existingStaff.setUpdateAt(LocalDate.now());

        // Chỉ set manager khi manager không null
        if (manager != null) {
            existingStaff.setManager(manager);
        } else {
            existingStaff.setManager(null);
        }

        // Lưu và trả về kết quả
        staffRepository.save(existingStaff);
        return staffMapper.toDto(existingStaff);
    }

    //    ---------------------------- DELETE STAFF-----------------------------------
    @Override
    @PreAuthorize("hasAuthority('DELETE_STAFF')")
    public boolean deleteStaff(String id) {
        if(staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //    ---------------------------- BAN/UNBANED STAFF-----------------------------------
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_STAFF_STATUS')")
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
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_STAFF_PASSWORD')")
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
