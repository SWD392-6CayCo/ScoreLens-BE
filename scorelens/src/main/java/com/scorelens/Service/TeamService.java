package com.scorelens.Service;

import com.scorelens.DTOs.Request.TeamRequest;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.Team;
import com.scorelens.Repository.BilliardMatchRepository;
import com.scorelens.Repository.TeamRepository;
import com.scorelens.Service.Interface.ITeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService implements ITeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private BilliardMatchRepository matchRepository;

    @Override
    public TeamResponse getById(Integer id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return toResponse(team);
    }

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        Team team = new Team();
        team.setName(request.getName());
        team.setTotalScore(request.getTotalScore());
        team.setCreateAt(request.getCreateAt());
        team.setStatus(request.getStatus());
        team.setBilliardMatch(match);

        return toResponse(teamRepository.save(team));
    }

    @Override
    public TeamResponse updateTeam(Integer id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        team.setName(request.getName());
        team.setTotalScore(request.getTotalScore());
        team.setCreateAt(request.getCreateAt());
        team.setStatus(request.getStatus());
        team.setBilliardMatch(match);

        return toResponse(teamRepository.save(team));
    }

    @Override
    public Integer delete(Integer id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException("Team not found");
        }
        teamRepository.deleteById(id);
        return id;
    }

    private TeamResponse toResponse(Team team) {
        TeamResponse res = new TeamResponse();
        res.setTeamID(team.getTeamID());
        res.setName(team.getName());
        res.setTotalScore(team.getTotalScore());
        res.setCreateAt(team.getCreateAt());
        res.setStatus(team.getStatus());
        res.setBilliardMatch(team.getBilliardMatch());
        return res;
    }
}
