package com.scorelens.Repository;

import com.scorelens.Entity.BilliardMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BilliardMatchRepository extends JpaRepository<BilliardMatch, Integer> {
    List<BilliardMatch> findByCustomer_CustomerID(String id);
    List<BilliardMatch> findByStaff_StaffID(String id);

    @Query(value = """
        SELECT DISTINCT bm.* FROM billiard_match bm
        JOIN team t ON t.billiard_matchid = bm.billiardmatchid
        JOIN player p ON p.teamid = t.teamid
        WHERE p.playerid = :playerId
        """, nativeQuery = true)
    List<BilliardMatch> findAllMatchesByPlayerId(@Param("playerId") int playerId);
}
