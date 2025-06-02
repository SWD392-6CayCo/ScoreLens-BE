package com.scorelens.Service;

import com.scorelens.DTOs.Request.CustomerRequestDto;
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

    public List<Customer> findAll() {return customerRepo.findAll();}

    public Optional<Customer> findById(String id) {return customerRepo.findById(id);}

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
    public Customer update(String id, Customer newCustomer) {
        return customerRepo.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(newCustomer.getName());
                    existingCustomer.setEmail(newCustomer.getEmail());
                    existingCustomer.setPhoneNumber(newCustomer.getPhoneNumber());
                    existingCustomer.setPassword(newCustomer.getPassword());
                    existingCustomer.setDob(newCustomer.getDob());
                    existingCustomer.setCreateAt(newCustomer.getCreateAt());
                    existingCustomer.setUpdateAt(newCustomer.getUpdateAt());
                    existingCustomer.setType(newCustomer.getType());
                    existingCustomer.setStatus(newCustomer.getStatus());
                    return customerRepo.save(existingCustomer);
                })
                .orElse(null);
    }

    //-------------------------------- CREATE ---------------------------------
    public Customer createCustomer(CustomerRequestDto request){
        if(customerRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if(customerRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        //Bắt IllegalArgumentException
        Customer customer = customerMapper.toEntity(request);
        customer.setCreateAt(LocalDate.now());
        customer.setStatus("active");
        customer.setType("normal");
        //upload ảnh...

        //Dùng BCrypt để mã hóa mật khẩu khi lưu vào DB
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        return customerRepo.save(customer);
    }
}
