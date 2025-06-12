package com.scorelens.Repository;

import com.scorelens.Entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepo extends JpaRepository<Round, Integer> {
}
