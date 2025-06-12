package com.scorelens.Service.Interface;


import com.scorelens.DTOs.Request.BilliardMatchRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;

import java.util.List;

public interface IBilliardMatchService {
        BilliardMatchResponse getById(Integer id);
        BilliardMatchResponse createMatch(BilliardMatchRequest request);
        BilliardMatchResponse updateMatch(Integer id, BilliardMatchRequest request);
        Integer delete(Integer id);
}
