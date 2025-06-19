package com.scorelens.Service;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Request.TeamCreateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Player;
import com.scorelens.Entity.Team;
import com.scorelens.Enums.ResultStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.PlayerMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.PlayerRepo;
import com.scorelens.Repository.TeamRepository;
import com.scorelens.Service.Interface.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerService implements IPlayerService {

    @Autowired
    PlayerRepo playerRepo;
    @Autowired
    TeamRepository teamRepo;
    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    PlayerMapper playerMapper;

    @Override
    public List<PlayerResponse> getAllPlayers() {
        List<Player> players = playerRepo.findAll();
        return playerMapper.toDto(players);
    }

    @Override
    public List<PlayerResponse> getByTeam(Integer id) {
        Team team = teamRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
        List<Player> players = playerRepo.findByTeam_TeamID(id);
        return playerMapper.toDto(players);
    }

    @Override
    public PlayerResponse getPlayerById(int id) {
        Player player = playerRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));
        return playerMapper.toDto(player);
    }

    @Override
    public Player createPlayer(PlayerCreateRequest request) {
        Team team = teamRepo.findById(request.getTeamID())
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
        Player player = new Player();
        player.setTeam(team);
        player.setName(request.getName());
        player.setStatus(ResultStatus.draw);
        player.setTotalScore(0);
        player.setCreateAt(LocalDateTime.now());
        player.setCustomer(null);
        return playerRepo.save(player);
    }

    @Override
    public PlayerResponse updatePlayer(Integer id, PlayerUpdateRequest request){
        Player player = playerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));

        player.setName(request.getName());
        player.setStatus(request.getStatus());
        player.setTotalScore(request.getTotalScore());
        Customer customer = customerRepo.findById(request.getCustomerID())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        player.setCustomer(customer);
        return playerMapper.toDto(playerRepo.save(player));
    }

    @Override
    public Integer delete(Integer id) {
        if (!playerRepo.existsById(id)) {
            throw new AppException(ErrorCode.TEAM_NOT_FOUND);
        }
        playerRepo.deleteById(id);
        return id;
    }

    @Override
    public void deletePlayer(int id) {
        playerRepo.deleteById(id);
    }
}
