package com.scorelens.Service;

import com.scorelens.DTOs.Request.CustomerSaveRequest;
import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.Entity.*;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.eclipse.collections.impl.block.factory.StringPredicates.matches;

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

    @Override
    public PlayerResponse updateCustomer(Integer id, CustomerSaveRequest request) {   // email or phone number
        Player player = playerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));
        Team team = player.getTeam();
        BilliardMatch match = team.getBilliardMatch();
        if (player.getCustomer() != null) {
            throw new AppException(ErrorCode.PLAYER_SAVED);
        }
        for (Team t : match.getTeams()) {
            for (Player p : t.getPlayers()) {
                if (p.getCustomer().getEmail().equals(request.getInfo()) ||
                    p.getCustomer().getPhoneNumber().equals(request.getInfo())) {
                    throw new AppException(ErrorCode.CUSTOMER_SAVED);
                }
            }
        }
        if (request.getInfo().matches("\\d+")){
            Customer c = customerRepo.findByPhoneNumber(request.getInfo())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            player.setCustomer(c);
            player.setName(c.getName());
        }else{
            Customer c = customerRepo.findByEmail(request.getInfo())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            player.setCustomer(c);
            player.setName(c.getName());
        }
        return playerMapper.toDto(playerRepo.save(player));
    }

    public Player getPlayer(Integer id) {
        return playerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));
    }

}
