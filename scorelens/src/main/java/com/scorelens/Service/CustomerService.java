package com.scorelens.Service;

import com.scorelens.DTOs.Request.ChangePasswordRequestDto;
import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Request.CustomerUpdateRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Enums.StatusType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Service.Interface.ICustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerService implements ICustomerService {

    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    StaffRepository staffRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserValidatorService userValidatorService;

    //-------------------------------- GET ---------------------------------
    @Override
    public List<CustomerResponseDto> findAll() {
        List<Customer> customers = customerRepo.findAll();
        if(customers.isEmpty()){
                throw new AppException(ErrorCode.EMPTY_LIST);
        }
        return customerMapper.toDtoList(customers);
    }

    @Override
    public CustomerResponseDto findById(String id) {
        Optional<Customer> optionalCus = customerRepo.findById(id);
        if (optionalCus.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        CustomerResponseDto responseDto = customerMapper.toDto(optionalCus.get());
        return responseDto;
    }

    //-------------------------------- DELETE ---------------------------------
    @Override
    public boolean deleteById(String id) {
        if(customerRepo.existsById(id)) {
            customerRepo.deleteById(id);
            return true;
        }
        return false;
    }

    //-------------------------------- UPDATE ---------------------------------
    @Override
    public CustomerResponseDto updateCustomer(String id, CustomerUpdateRequestDto requestDto) {
        //Lấy ra Customer cần update
        Customer customer = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        //Check email & phone validation
        userValidatorService.validateEmailUnique(requestDto.getEmail(), customer.getEmail());
        userValidatorService.validatePhoneUnique(requestDto.getPhoneNumber(), customer.getPhoneNumber());

        //call updateEntity func() in MapStuct to map requestDto into Entity
        customerMapper.updateEntity(customer, requestDto);
        // Mã hóa password
        //customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        customer.setUpdateAt(LocalDate.now());
        customerRepo.save(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(customer);
        return responseDto;
    }

    //-------------------------------- CREATE ---------------------------------
    @Override
    public CustomerResponseDto createCustomer(CustomerCreateRequestDto request){
        //Kiểm tra xem Email và PhoneNumber đã đc sử dụng hay chưa-------
        userValidatorService.validateEmailAndPhoneUnique(request.getEmail(), request.getPhoneNumber());
        //----------------------------------------------------------------

        //Map từ dto sang Entity
        Customer customer = customerMapper.toEntity(request);

        //set các giá trị còn lại của customer
        customer.setCreateAt(LocalDate.now());
        customer.setStatus(StatusType.active);
        customer.setType("normal");
        //upload ảnh...

        //mã hóa password
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        customerRepo.save(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(customer);
        return responseDto;
    }

    //-------------------------------- UPDATE STATUS BANED/UNBANED ---------------------------------
    @Override
    public boolean updateCustomerStatus(String id, String status) {
        boolean check = true;
        Customer c = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));
        if (!status.equalsIgnoreCase(StatusType.active.toString()) && !status.equalsIgnoreCase(StatusType.inactive.toString())) {
            throw new AppException(ErrorCode.INVALID_STATUS); // Optional: bạn có thể thêm enum hoặc custom error code
        }
        c.setStatus(StatusType.valueOf(status));
        c.setUpdateAt(LocalDate.now());
        customerRepo.save(c);

        return check;
    }

    //    ---------------------------- UPDATE PASSWORD-----------------------------------
    @Override
    public boolean updatePassword (String id, ChangePasswordRequestDto requestDto){
        boolean check = false;
        Customer c = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));

        String cusPassword = c.getPassword();
        if(passwordEncoder.matches(requestDto.getOldPassword(), cusPassword)){ //password nhập vào đúng với pass của user
            //cho phép đổi
            if(requestDto.getOldPassword().equals(requestDto.getNewPassword())){ //pass mới trùng với pass cũ
                throw new AppException(ErrorCode.DUPLICATED_PASSWORD);
            }
            //pass mới khác pass cũ
            c.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            customerRepo.save(c);
            check = true;
        } else {
            //không cho phép đổi
            System.out.println(requestDto.getOldPassword());
            System.out.println(cusPassword);
            throw new AppException(ErrorCode.NOT_MATCH_PASSWORD);

        }
        return check;
    }
    //    --------------------------------------------------------------------------

}
