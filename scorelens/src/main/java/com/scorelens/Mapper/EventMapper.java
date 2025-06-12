package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.Event;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventResponse toEventResponse(Event event);
    Event toEvent(EventResponse eventResponse);
    Event toEventRequest(EventRequest eventRequest);
    List<EventResponse> toEventResponses(List<Event> events);



}
