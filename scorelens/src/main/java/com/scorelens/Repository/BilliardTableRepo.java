package com.scorelens.Repository;

import com.scorelens.Entity.BilliardTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BilliardTableRepo extends JpaRepository<BilliardTable, String> {
    List<BilliardTable> findAllByStore_StoreID(String storeID);

}
