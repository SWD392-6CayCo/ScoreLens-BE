package com.scorelens.Repository;

import com.scorelens.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepo extends JpaRepository<Event, Integer> {
    List<Event> findAllByGameSet_GameSetID(int gameSetID);
    List<Event> findAllByPlayer_PlayerID(int playerID);
    List<Event> findAllByGameSet_GameSetIDAndPlayer_PlayerID(int gameSetID, int playerID);
}
