package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.GameSetCreateRequest;
import com.scorelens.DTOs.Request.GameSetUpdateRequest;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.GameSet;
import com.scorelens.Entity.Team;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameSetMapper {

//    @Mapping(target = "gameSetID", ignore = true)
//    @Mapping(target = "gameSetNo", ignore = true)
//    @Mapping(target = "winner", ignore = true)
//    @Mapping(target = "startTime", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "endTime", ignore = true)
//    @Mapping(target = "status", constant = "pending")
//    @Mapping(target = "billiardMatchID", source = "billiardMatchID")
    GameSet toGameSet(GameSetCreateRequest request);

    @Mapping(target = "billiardMatchID", source = "billiardMatch.billiardMatchID")
    GameSetResponse toGameSetResponse(GameSet gameSet);

    @Mapping(target = "billiardMatchID", source = "billiardMatch.billiardMatchID")
    List<GameSetResponse> toSetResponseList(List<GameSet> sets);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGameSetFromRequest(GameSetUpdateRequest request, @MappingTarget GameSet gameSet);
}
