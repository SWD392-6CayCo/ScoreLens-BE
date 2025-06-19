package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Player;
import com.scorelens.Entity.Team;
import com.scorelens.Enums.ResultStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

public interface IPlayerService {
    public List<PlayerResponse> getAllPlayers();
    public List<PlayerResponse> getByTeam(Integer id);
    public PlayerResponse getPlayerById(int id);

    public Player createPlayer(PlayerCreateRequest request);

    public PlayerResponse updatePlayer(Integer id, PlayerUpdateRequest request);
    public Integer delete(Integer id) ;
    public void deletePlayer(int id) ;
}
