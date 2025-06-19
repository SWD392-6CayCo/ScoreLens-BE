package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Request.TeamCreateRequest;
import com.scorelens.DTOs.Request.TeamUpdateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.Player;
import com.scorelens.Entity.Team;
import org.mapstruct.*;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    Player toPlayer(PlayerCreateRequest request);

    @Mapping(target = "teamID", source = "team.teamID")
    @Mapping(target = "customerID", source = "customer.customerID")
    PlayerResponse toDto(Player player);

    @Mapping(target = "teamID", source = "team.teamID")
    @Mapping(target = "customerID", source = "customer.customerID")
    List<PlayerResponse> toDto(List<Player> players);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Player player, PlayerUpdateRequest request);
}
