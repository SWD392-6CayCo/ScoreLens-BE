package com.scorelens.Repository;

import com.scorelens.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {
    //kiểm tra xem email đã tồn tại trong hệ thống chưa? True -> Đã có, False -> Chưa
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);

}
