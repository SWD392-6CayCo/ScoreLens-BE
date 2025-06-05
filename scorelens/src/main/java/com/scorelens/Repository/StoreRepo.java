package com.scorelens.Repository;

import com.scorelens.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepo extends JpaRepository<Store, String> {
    boolean existsByname(String name);
}
