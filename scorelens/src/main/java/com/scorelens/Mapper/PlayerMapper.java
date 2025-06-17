package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.Entity.Player;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    Player toPlayer(PlayerCreateRequest request);
    PlayerResponse toDto(Player player);
    List<PlayerResponse> toDto(List<Player> players);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Player player, PlayerUpdateRequest request);
}
