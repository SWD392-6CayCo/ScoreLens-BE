package com.scorelens.Repository;

import com.scorelens.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepo extends JpaRepository<Store, String> {
    boolean existsByname(String name);

    @Query("SELECT p.customer.customerID, p.customer.name, COUNT(p) " +
            "FROM Player p " +
            "WHERE p.customer IS NOT NULL AND p.team.billiardMatch.billardTable.store.storeID = :storeId " +
            "GROUP BY p.customer.customerID, p.customer.name " +
            "ORDER BY COUNT(p) DESC")
    List<Object[]> countMatchesByCustomerInStore(@Param("storeId") String storeId);

}
