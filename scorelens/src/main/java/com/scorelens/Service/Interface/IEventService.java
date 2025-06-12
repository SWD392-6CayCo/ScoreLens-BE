package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Response.EventResponse;

import java.util.List;

public interface IEventService {

    EventResponse addEvent(EventRequest eventRequest);
    List<EventResponse> getAllEvents();
    List<EventResponse> getEventsByPlayerID(int playerID);
    List<EventResponse> getEventsByRoundID(int roundID);
    boolean deleteEventByPlayerID(int playerID);
    boolean deleteEventByRoundID(int roundID);
    List<EventResponse> getEventsByPlayerIDAndRoundID(int playerID, int roundID);


}
