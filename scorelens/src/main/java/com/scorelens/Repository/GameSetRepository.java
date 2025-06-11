package com.scorelens.Repository;

import com.scorelens.Entity.GameSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSetRepository extends JpaRepository<GameSet, Integer> {}

