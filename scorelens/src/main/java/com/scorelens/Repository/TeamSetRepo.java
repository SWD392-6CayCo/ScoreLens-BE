package com.scorelens.Repository;

import com.scorelens.Entity.GameSet;
import com.scorelens.Entity.Team;
import com.scorelens.Entity.TeamSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamSetRepo extends JpaRepository<TeamSet, Integer> {
    List<TeamSet> findByTeam_TeamID(int teamID);
    List<TeamSet> findByGameSet_GameSetID(int gameSetID);
    TeamSet findByGameSetAndTeam(GameSet gameSet, Team team);

}
