package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.GameSetRequest;
import com.scorelens.DTOs.Response.GameSetResponse;

public interface IGameSetService {
    GameSetResponse getById(Integer id);
    GameSetResponse createSet(GameSetRequest request);
    GameSetResponse updateSet(Integer id, GameSetRequest request);
    Integer delete(Integer id);
}
