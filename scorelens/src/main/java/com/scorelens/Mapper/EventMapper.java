package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "player.playerID", target = "playerID")
    @Mapping(source = "gameSet.gameSetID", target = "gameSetID")
    EventResponse toEventResponse(Event event);

    @Mapping(source = "playerID", target = "player.playerID")
    @Mapping(source = "gameSetID", target = "gameSet.gameSetID")
    Event toEvent(EventResponse eventResponse);

    @Mapping(source = "playerID", target = "player.playerID")
    @Mapping(source = "gameSetID", target = "gameSet.gameSetID")
    Event toEventRequest(EventRequest eventRequest);

    @Mapping(source = "player.playerID", target = "playerID")
    @Mapping(source = "gameSet.gameSetID", target = "gameSetID")
    List<EventResponse> toEventResponses(List<Event> events);



}
