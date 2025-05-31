package com.scorelens.Repository;


import com.scorelens.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, String> {

    Optional<Customer> findFirstBy();
}
