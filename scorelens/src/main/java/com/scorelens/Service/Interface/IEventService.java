package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.Event;

import java.util.List;

public interface IEventService {

    EventResponse addEvent(EventRequest eventRequest);
    List<EventResponse> getAllEvents();
    List<EventResponse> getEventsByPlayerID(int playerID);
    List<EventResponse> getEventsByGameSetID(int gameSetID);
    boolean deleteEventByPlayerID(int playerID);
    boolean deleteEventByGameSetID(int gameSetID);
    List<EventResponse> getEventsByPlayerIDAndGameSetID(int playerID, int gameSetID);
    Event geteventByID(int eventID);
    int countEventsGameSetID(int gameSetID);
}
