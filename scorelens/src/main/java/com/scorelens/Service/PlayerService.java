package com.scorelens.Service;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.Entity.Player;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.PlayerMapper;
import com.scorelens.Repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    PlayerRepo playerRepo;

    @Autowired
    PlayerMapper playerMapper;

    public List<PlayerResponse> getAllPlayers() {
        List<Player> players = playerRepo.findAll();
        return playerMapper.toDto(players);
    }

    public PlayerResponse getPlayerById(int id) {
        Player player = playerRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));
        return playerMapper.toDto(player);
    }

    public PlayerResponse createPlayer(PlayerCreateRequest request) {
        Player p = playerMapper.toPlayer(request);
        p.setCreateAt(LocalDate.now());
        playerRepo.save(p);
        return playerMapper.toDto(p);
    }

    public PlayerResponse updatePlayer(PlayerUpdateRequest request){
        Player p = playerRepo.findById(request.getPlayerID())
                .orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));
        playerMapper.update(p, request);
        return playerMapper.toDto(p);
    }
    public void deletePlayer(int id) {
        playerRepo.deleteById(id);
    }
}
