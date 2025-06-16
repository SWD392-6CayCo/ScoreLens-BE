package com.scorelens.Service.Interface;


import com.scorelens.DTOs.Request.BilliardMatchCreateRequest;
import com.scorelens.DTOs.Request.BilliardMatchUpdateRequest;
import com.scorelens.DTOs.Request.GameSetCreateRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;

public interface IBilliardMatchService {
        BilliardMatchResponse getById(Integer id);
        BilliardMatchResponse createMatch(BilliardMatchCreateRequest request);
        BilliardMatchResponse updateMatch(Integer id, BilliardMatchUpdateRequest request);
        Integer delete(Integer id);
}
