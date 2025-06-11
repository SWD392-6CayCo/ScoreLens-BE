package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.TeamRequest;
import com.scorelens.DTOs.Response.TeamResponse;

public interface ITeamService {
        TeamResponse getById(Integer id);
        TeamResponse createTeam(TeamRequest request);
        TeamResponse updateTeam(Integer id, TeamRequest request);
        Integer delete(Integer id);
}
