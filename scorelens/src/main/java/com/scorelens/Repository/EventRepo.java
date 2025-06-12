package com.scorelens.Repository;

import com.scorelens.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepo extends JpaRepository<Event, Integer> {
    List<Event> findAllByRound_RoundID(int roundID);
    List<Event> findAllByPlayer_PlayerID(int playerID);
    List<Event> findAllByRound_RoundIDAndPlayer_PlayerID(int roundID, int playerID);
}
