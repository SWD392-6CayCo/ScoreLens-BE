package com.scorelens.Service.Interface;


import com.scorelens.DTOs.Request.BilliardMatchCreateRequest;
import com.scorelens.DTOs.Request.BilliardMatchUpdateRequest;
import com.scorelens.DTOs.Request.MatchFilterRequest;
import com.scorelens.DTOs.Request.ScoreRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.Entity.BilliardMatch;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface IBilliardMatchService {
        BilliardMatchResponse getById(Integer id);
        BilliardMatchResponse createMatch(BilliardMatchCreateRequest request);
        BilliardMatchResponse updateMatch(Integer id, BilliardMatchUpdateRequest request);
        Integer delete(Integer id);
        List<BilliardMatchResponse> getByCustomer(String id);
        List<BilliardMatchResponse> getByStaff(String id);
        BilliardMatchResponse getByPlayerID(Integer id);
        List<BilliardMatchResponse> getByCustomerID(String id);
        BilliardMatchResponse updateScore(ScoreRequest request);
        BilliardMatchResponse cancelMatch(Integer id, Integer teamID);
        //BilliardMatchResponse cancel(Integer id);
        void deleteAll();
        List<BilliardMatchResponse> getFilter(MatchFilterRequest request);
        String completeMatch(Integer id);
        BilliardMatchResponse getOnGoingMatch(String billiardTableID);

}
