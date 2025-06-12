package com.scorelens.Repository;

import com.scorelens.Entity.BilliardMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilliardMatchRepo extends JpaRepository<BilliardMatch, Integer> {
}
