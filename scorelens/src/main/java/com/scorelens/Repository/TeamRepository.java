package com.scorelens.Repository;

import com.scorelens.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByBilliardMatch_BilliardMatchID(Integer id);

}

