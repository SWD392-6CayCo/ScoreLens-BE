package com.scorelens.Repository;


import com.scorelens.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, String> {
    //kiểm tra xem email đã tồn tại trong hệ thống chưa? True -> Đã có, False -> Chưa
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

}
