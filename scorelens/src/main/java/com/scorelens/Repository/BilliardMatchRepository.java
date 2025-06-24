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

//    @Query(value =
//            "SELECT bm.billiard_matchid, bm.start_time, bm.end_time, bm.status, bm.winner, " +
//                    "bm.set_up, bm.total_set, bm.modeid, bm.customerid, bm.staffid " +
//                    "FROM billiard_match bm " +
//                    "JOIN team t ON t.billiard_matchid = bm.billiard_matchid " +
//                    "JOIN player p ON p.teamid = t.teamid " +
//                    "WHERE p.playerid = :id", nativeQuery = true)
//    BilliardMatch findByPlayerId(@Param("id") Integer id);

    @Query("SELECT bm FROM BilliardMatch bm JOIN bm.teams t JOIN t.players p WHERE p.playerID = :id")
    BilliardMatch findByPlayerId(@Param("id") Integer id);

    @Query("SELECT bm FROM BilliardMatch bm JOIN bm.teams t JOIN t.players p WHERE p.customer.customerID = :id")
    List<BilliardMatch> findByCustomerId(@Param("id") String id);

//    @Query(value =
//            "SELECT bm.billiard_matchid, bm.start_time, bm.end_time, bm.status, bm.winner, " +
//                    "bm.set_up, bm.total_set, bm.modeid, bm.customerid, bm.staffid " +
//                    "FROM billiard_match bm " +
//                    "JOIN team t ON t.billiard_matchid = bm.billiard_matchid " +
//                    "JOIN player p ON p.teamid = t.teamid " +
//                    "WHERE p.customerid = :id", nativeQuery = true)
//    List<BilliardMatch> findByCustomerId(@Param("id") String id);
//    @Query(value = """
//        SELECT DISTINCT bm.* FROM billiard_match bm
//        JOIN team t ON t.billiard_matchid = bm.billiard_matchid
//        JOIN player p ON p.teamid = t.teamid
//        WHERE p.customerid = :id
//        """, nativeQuery = true)
//    List<BilliardMatch> findByCustomerId(@Param("id") String id);
}
