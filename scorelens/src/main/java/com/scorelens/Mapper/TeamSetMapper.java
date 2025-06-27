package com.scorelens.Mapper;

import com.scorelens.DTOs.Response.TeamSetResponse;
import com.scorelens.Entity.TeamSet;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamSetMapper {
    @Mapping(target = "teamID", source = "team.teamID")
    @Mapping(target = "gameSetID", source = "gameSet.gameSetID")
    TeamSetResponse toResponse(TeamSet teamSet);

    @Mapping(target = "teamID", source = "team.teamID")
    @Mapping(target = "gameSetID", source = "gameSet.gameSetID")
    List<TeamSetResponse> toResponseList(List<TeamSet> tss);

}
