package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.Entity.Player;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    Player toPlayer(PlayerCreateRequest request);

//    @Mapping(target = "teamID", source = "team.teamID")
//    @Mapping(target = "customerID", source = "customer.customerID")
//    PlayerResponse toDto(Player player);
//
//    @Mapping(target = "teamID", source = "team.teamID")
//    @Mapping(target = "customerID", source = "customer.customerID")
//    List<PlayerResponse> toDto(List<Player> players);

    @Mapping(target = "teamID", expression = "java(player.getTeam() != null ? player.getTeam().getTeamID() : null)")
    @Mapping(target = "customerID", expression = "java(player.getCustomer() != null ? player.getCustomer().getCustomerID() : null)")
    PlayerResponse toDto(Player player);

    @Mapping(target = "teamID", expression = "java(player.getTeam() != null ? player.getTeam().getTeamID() : null)")
    @Mapping(target = "customerID", expression = "java(player.getCustomer() != null ? player.getCustomer().getCustomerID() : null)")
    List<PlayerResponse> toDto(List<Player> players);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Player player, PlayerUpdateRequest request);
}
