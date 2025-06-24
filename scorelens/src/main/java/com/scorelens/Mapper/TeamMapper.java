package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.TeamCreateRequest;
import com.scorelens.DTOs.Request.TeamUpdateRequest;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.Team;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface TeamMapper {

//    @Mapping(target = "teamID", ignore = true)
//    @Mapping(target = "totalScore", constant = "0")
//    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "status", constant = "draw")
//    @Mapping(target = "billiardMatchID", source = "billiardMatchID")
    Team toTeam(TeamCreateRequest request);

    @Mapping(target = "billiardMatchID", source = "billiardMatch.billiardMatchID")
   // @Mapping(target = "players", expression = "java(playerMapper.toDto(team.getPlayers()))")
    TeamResponse toTeamResponse(Team team);

    @Mapping(target = "billiardMatchID", source = "billiardMatch.billiardMatchID")
    //@Mapping(target = "players", expression = "java(playerMapper.toDto(team.getPlayers()))")
    List<TeamResponse> toTeamResponseList(List<Team> teams);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTeam(TeamUpdateRequest request, @MappingTarget Team team);
}
