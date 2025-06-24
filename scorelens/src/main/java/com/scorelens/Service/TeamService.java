package com.scorelens.Service;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.TeamCreateRequest;
import com.scorelens.DTOs.Request.TeamUpdateRequest;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.Player;
import com.scorelens.Entity.Team;
import com.scorelens.Enums.ResultStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.PlayerMapper;
import com.scorelens.Mapper.TeamMapper;
import com.scorelens.Repository.BilliardMatchRepository;
import com.scorelens.Repository.TeamRepository;
import com.scorelens.Service.Interface.ITeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamService implements ITeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private BilliardMatchRepository matchRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    TeamMapper teamMapper;

    @Override
    public TeamResponse getById(Integer id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
        for (Player p : team.getPlayers()) {
            System.out.println("PlayerID: " + p.getPlayerID() + ", TeamID: " +
                    (p.getTeam() != null ? p.getTeam().getTeamID() : "null"));
        }

        return teamMapper.toTeamResponse(team);
    }

    @Override
    public List<TeamResponse> getByMatchID(Integer id) {
        BilliardMatch match = matchRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        List<Team> teams = teamRepository.findByBilliardMatch_BilliardMatchID(id);
        return teamMapper.toTeamResponseList(teams);
    }

    @Override
    public Team createTeam(TeamCreateRequest request) {
        BilliardMatch match = matchRepository.findById(request.getBilliardMatchID())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

        Team team = new Team();
        team.setName(request.getName());
        team.setTotalMember(request.getTotalMember());
        team.setTotalScore(0);
        team.setCreateAt(LocalDateTime.now());
        team.setStatus(ResultStatus.draw);
        team.setBilliardMatch(match);
        teamRepository.save(team);
        for (String name : request.getMemberNames()) {
            PlayerCreateRequest playerCreateRequest = new PlayerCreateRequest();
            playerCreateRequest.setName(name);
            playerCreateRequest.setTeamID(team.getTeamID());
            Player player = playerService.createPlayer(playerCreateRequest);
            player.setTeam(team);
            team.addPlayer(player);
        }
        return teamRepository.save(team);
    }

    @Override
    public TeamResponse updateTeam(Integer id, TeamUpdateRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));

        team.setName(request.getName());
        team.setTotalMember(request.getTotalMember());
        team.setTotalScore(request.getTotalScore());
        if (request.getStatus().equals(ResultStatus.lose)) {
            team.setStatus(ResultStatus.lose);
        }else {
            team.setStatus(ResultStatus.win);
        }
        return teamMapper.toTeamResponse(teamRepository.save(team));
    }

    @Override
    public Integer delete(Integer id) {
        if (!teamRepository.existsById(id)) {
            throw new AppException(ErrorCode.TEAM_NOT_FOUND);
        }
        teamRepository.deleteById(id);
        return id;
    }

    @Override
    public void deleteByMatch(Integer id) {
        if (!teamRepository.existsById(id)) {
            throw new AppException(ErrorCode.TEAM_NOT_FOUND);
        }
        teamRepository.deleteById(id);
    }
}
