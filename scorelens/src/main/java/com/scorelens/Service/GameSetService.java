package com.scorelens.Service;


import com.scorelens.DTOs.Request.GameSetRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.GameSet;
import com.scorelens.Repository.BilliardMatchRepository;
import com.scorelens.Repository.GameSetRepository;
import com.scorelens.Service.Interface.IGameSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameSetService implements IGameSetService {

    @Autowired
    private GameSetRepository gameSetRepository;

    @Autowired
    private BilliardMatchRepository matchRepository;

    @Override
    public GameSetResponse getById(Integer id) {
        GameSet gameSet = gameSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GameSet not found with ID: " + id));
        return toResponse(gameSet);
    }

    @Override
    public GameSetResponse createSet(GameSetRequest request) {
        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        GameSet gameSet = new GameSet();
        gameSet.setGameSetNo(request.getGameSetNo());
        gameSet.setRaceTo(request.getRaceTo());
        gameSet.setWinner(request.getWinner());
        gameSet.setStartTime(request.getStartTime());
        gameSet.setEndTime(request.getEndTime());
        gameSet.setStatus(request.getStatus());
        gameSet.setBilliardMatch(match);

        return toResponse(gameSetRepository.save(gameSet));
    }

    @Override
    public GameSetResponse updateSet(Integer id, GameSetRequest request) {
        GameSet gameSet = gameSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GameSet not found"));

        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        gameSet.setGameSetNo(request.getGameSetNo());
        gameSet.setRaceTo(request.getRaceTo());
        gameSet.setWinner(request.getWinner());
        gameSet.setStartTime(request.getStartTime());
        gameSet.setEndTime(request.getEndTime());
        gameSet.setStatus(request.getStatus());
        gameSet.setBilliardMatch(match);

        return toResponse(gameSetRepository.save(gameSet));
    }

    @Override
    public Integer delete(Integer id) {
        if (!gameSetRepository.existsById(id)) {
            throw new RuntimeException("GameSet not found");
        }
        gameSetRepository.deleteById(id);
        return id;
    }

    private GameSetResponse toResponse(GameSet gameSet) {
        GameSetResponse res = new GameSetResponse();
        res.setGameSetID(gameSet.getGameSetID());
        res.setGameSetNo(gameSet.getGameSetNo());
        res.setRaceTo(gameSet.getRaceTo());
        res.setWinner(gameSet.getWinner());
        res.setStartTime(gameSet.getStartTime());
        res.setEndTime(gameSet.getEndTime());
        res.setStatus(gameSet.getStatus());
        res.setBilliardMatch(gameSet.getBilliardMatch());
        return res;
    }

}
