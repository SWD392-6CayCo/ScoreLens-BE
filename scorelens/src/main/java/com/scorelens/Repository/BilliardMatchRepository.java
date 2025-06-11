package com.scorelens.Repository;

import com.scorelens.Entity.BilliardMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BilliardMatchRepository extends JpaRepository<BilliardMatch, Integer> {

}
