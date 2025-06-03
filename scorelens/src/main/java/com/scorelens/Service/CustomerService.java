package com.scorelens.Service;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Enums.StatusType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Service.Interface.ICustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public CustomerResponseDto updateCustomer(String id, CustomerRequestDto requestDto) {
        //Lấy ra Customer cần update
        Customer customer = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        if(customerRepo.existsByEmail(requestDto.getEmail()) && !customer.getEmail().equals(requestDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if(customerRepo.existsByPhoneNumber(requestDto.getPhoneNumber()) && !customer.getPhoneNumber().equals(requestDto.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        // Mã hóa password
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        //Map dto sang entity
        customer = customerMapper.toEntity(requestDto);
        customer.setUpdateAt(LocalDate.now());
        customerRepo.save(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(customer);
        return responseDto;
    }

    //-------------------------------- CREATE ---------------------------------
    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto request){
        //Kiểm tra xem Email và PhoneNumber đã đc sử dụng hay chưa-------
        if(customerRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if(customerRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        //----------------------------------------------------------------

        //Map từ dto sang Entity
        Customer customer = customerMapper.toEntity(request);

        //set các giá trị còn lại của customer
        customer.setCreateAt(LocalDate.now());
        customer.setStatus(StatusType.active);
        customer.setType("normal");
        //upload ảnh...

        //Dùng BCrypt để mã hóa mật khẩu khi lưu vào DB
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
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
}
