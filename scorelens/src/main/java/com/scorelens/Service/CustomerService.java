package com.scorelens.Service;

import com.scorelens.DTOs.Request.CustomerRequestDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Repository.CustomerRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerService {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    CustomerMapper customerMapper;

    public List<CustomerResponseDto> findAll() {
        return customerMapper.toResponseDtoList(customerRepo.findAll());
    }

    public CustomerResponseDto findById(String id) {
        Optional<Customer> optionalCus = customerRepo.findById(id);
        CustomerResponseDto responseDto = customerMapper.toResponseDto(optionalCus.get());
        return responseDto;
    }

    public Customer save(Customer customer) {
        return customerRepo.save(customer);
    }

    public boolean deleteById(String id) {
        if(customerRepo.existsById(id)) {
            customerRepo.deleteById(id);
            return true;
        }
        return false;
    }

    //-------------------------------- UPDATE ---------------------------------
    public Customer update(String id, CustomerRequestDto requestDto) {
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
        customerMapper.toEntity(customer,requestDto);

        return customerRepo.save(customer);
    }

    //-------------------------------- CREATE ---------------------------------
    public Customer createCustomer(CustomerRequestDto request){
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
        customer.setStatus("active");
        customer.setType("normal");
        //upload ảnh...

        //Dùng BCrypt để mã hóa mật khẩu khi lưu vào DB
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        return customerRepo.save(customer);
    }

    public boolean updateCustomerStatus(String id, String status) {
        boolean check = true;
        Customer c = customerRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST));
        if (!status.equalsIgnoreCase("active") && !status.equalsIgnoreCase("inactive")) {
            throw new AppException(ErrorCode.INVALID_STATUS); // Optional: bạn có thể thêm enum hoặc custom error code
        }
        c.setStatus(status.toLowerCase());
        customerRepo.save(c);

        return check;
    }
}
