package com.scorelens.Repository;

import com.scorelens.Entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepo extends JpaRepository<Player, Integer> {
    List<Player> findByTeam_TeamID(int id);
}
