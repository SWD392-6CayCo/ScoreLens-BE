package com.scorelens.Service;


import com.scorelens.DTOs.Request.GameSetCreateRequest;
import com.scorelens.DTOs.Request.GameSetUpdateRequest;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.GameSet;
import com.scorelens.Entity.Team;
import com.scorelens.Entity.TeamSet;
import com.scorelens.Enums.MatchStatus;
import com.scorelens.Enums.ResultStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.GameSetMapper;
import com.scorelens.Repository.BilliardMatchRepository;
import com.scorelens.Repository.GameSetRepository;
import com.scorelens.Repository.TeamRepository;
import com.scorelens.Service.Interface.IGameSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class GameSetService implements IGameSetService {

    @Autowired
    private GameSetRepository gameSetRepository;
    @Autowired
    private BilliardMatchRepository matchRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamSetService tsService;

    @Autowired
    TeamSetService teamSetService;
    @Autowired
    private GameSetMapper gameSetMapper;

    @Override
    public List<GameSetResponse> getAllGameSets() {
        List<GameSet> gss = gameSetRepository.findAll();
        return gameSetMapper.toSetResponseList(gss);
    }

    @Override
    public GameSetResponse getById(Integer id) {
        GameSet gameSet = gameSetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
        return gameSetMapper.toGameSetResponse(gameSet);
    }

    @Override
    public List<GameSetResponse> getByMatchID(Integer id) {
        BilliardMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        List<GameSet> sets = gameSetRepository.findByBilliardMatch_BilliardMatchID(id);
        return gameSetMapper.toSetResponseList(sets);
    }

    public List<GameSet> getByMatch(int id) {
        BilliardMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        return gameSetRepository.findByBilliardMatch_BilliardMatchID(id);
    }

    @Override
    public GameSetResponse createSet(GameSetCreateRequest request) {
        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

        GameSet gameSet = new GameSet();
        GameSet maxGameSet = match.getSets().stream()
                .max(Comparator.comparingInt(GameSet::getGameSetNo))
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
        gameSet.setGameSetNo(maxGameSet.getGameSetNo() + 1);
        gameSet.setRaceTo(request.getRaceTo());
        gameSet.setWinner(null);
        gameSet.setStartTime(LocalDateTime.now());
        gameSet.setEndTime(null);
        gameSet.setStatus(MatchStatus.pending);
        gameSet.setBilliardMatch(match);
        gameSetRepository.save(gameSet);

        match.setTotalSet(match.getTotalSet() + 1);
        matchRepository.save(match);

        for (Team t : match.getTeams()) {
            TeamSet ts = teamSetService.createTeamSet(t.getTeamID(), gameSet.getGameSetID());
            gameSet.addTeamSet(ts);
        }

        return gameSetMapper.toGameSetResponse(gameSetRepository.save(gameSet));
    }

    @Override
    public GameSet createSetInMatch(Integer i,GameSetCreateRequest request) {
        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

        GameSet gameSet = new GameSet();
        gameSet.setGameSetNo(i);
        gameSet.setRaceTo(request.getRaceTo());
        gameSet.setWinner(null);
        gameSet.setStartTime(LocalDateTime.now());
        gameSet.setEndTime(null);
        gameSet.setStatus(MatchStatus.pending);
        gameSet.setBilliardMatch(match);

        return gameSetRepository.save(gameSet);
    }

    @Override
    public GameSetResponse updateSet(Integer id, GameSetUpdateRequest request) {
        GameSet gameSet = gameSetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));

        gameSet.setRaceTo(request.getRaceTo());
        gameSet.setWinner(request.getWinner());
        if(request.getStatus().equals(MatchStatus.ongoing)) {
            gameSet.setStatus(MatchStatus.ongoing);
        }else if(request.getStatus().equals(MatchStatus.completed)) {
            gameSet.setStatus(MatchStatus.completed);
        }else {
            gameSet.setStatus(MatchStatus.forfeited);
        }
        return gameSetMapper.toGameSetResponse(gameSetRepository.save(gameSet));
    }

    @Override
    public Integer delete(Integer id) {
        if (!gameSetRepository.existsById(id)) {
            throw new AppException(ErrorCode.SET_NOT_FOUND);
        }
        gameSetRepository.deleteById(id);
        return id;
    }

    @Override
    public void deleteByMatch(Integer id) {
        if (!gameSetRepository.existsById(id)) {
            throw new AppException(ErrorCode.SET_NOT_FOUND);
        }
        gameSetRepository.deleteById(id);
    }

    @Override
    public GameSetResponse cancel(Integer id) {
        GameSet gameSet = gameSetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
        gameSet.setStatus(MatchStatus.cancelled);
        gameSetRepository.save(gameSet);
        return gameSetMapper.toGameSetResponse(gameSet);
    }

//    public String completeSet(Integer id) {
//        GameSet gameSet = gameSetRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
//        gameSet.setStatus(MatchStatus.completed);
//        gameSetRepository.save(gameSet);
//        return "GameSet with ID " + id + " has been completed";
//    }

    @Override
    public GameSet startSet(int id){
        GameSet gameSet = gameSetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));
        gameSet.setStartTime(LocalDateTime.now());
        gameSet.setStatus(MatchStatus.ongoing);
        gameSetRepository.save(gameSet);
        log.info("GameSet no " + gameSet.getGameSetNo() + " has been started");
        return gameSet;
    }




}
