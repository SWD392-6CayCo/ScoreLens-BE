package com.scorelens.Service;

import com.scorelens.Entity.GameSet;
import com.scorelens.Entity.Team;
import com.scorelens.Entity.TeamSet;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Repository.GameSetRepository;
import com.scorelens.Repository.TeamRepository;
import com.scorelens.Repository.TeamSetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamSetService {
    @Autowired
    TeamSetRepo teamSetRepo;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    GameSetRepository gameSetRepository;


    public TeamSet createTeamSet(int teamID, int gameSetID) {
        Team t = teamRepository.findById(teamID)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
        GameSet gs = gameSetRepository.findById(gameSetID)
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
        TeamSet ts = new TeamSet();
        ts.setTeam(t);
        ts.setGameSet(gs);
        ts.setTotalScore(0);
        return teamSetRepo.save(ts);
    }

    public TeamSet updateTeamSet(int teamID, int gameSetID, int score) {
        Team t = teamRepository.findById(teamID)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
        GameSet gs = gameSetRepository.findById(gameSetID)
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
        TeamSet ts = teamSetRepo.findByGameSetAndTeam(gs,t);
        ts.setTotalScore(score);
        return teamSetRepo.save(ts);
    }
}
