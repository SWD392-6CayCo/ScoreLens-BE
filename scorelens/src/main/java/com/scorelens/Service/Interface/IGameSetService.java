package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.GameSetCreateRequest;
import com.scorelens.DTOs.Request.GameSetUpdateRequest;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.GameSet;

import java.util.List;

public interface IGameSetService {
    GameSetResponse getById(Integer id);
    List<GameSetResponse> getSetsByMatchID(Integer id);
    GameSetResponse createSet(GameSetCreateRequest request);
    GameSet createSetInMatch(Integer i, GameSetCreateRequest request);
    GameSetResponse updateSet(Integer id, GameSetUpdateRequest request);
    Integer delete(Integer id);
}
