package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.TeamCreateRequest;
import com.scorelens.DTOs.Request.TeamUpdateRequest;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.Team;

import java.util.List;

public interface ITeamService {
        TeamResponse getById(Integer id);
        Team createTeam(TeamCreateRequest request);
        TeamResponse updateTeam(Integer id, TeamUpdateRequest request);
        List<TeamResponse> getByMatchID(Integer id);
        Integer delete(Integer id);
        void deleteByMatch(Integer id);
}
