package com.scorelens.Repository;

import com.scorelens.Entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepo extends JpaRepository<Player, Integer> {
}
