package com.scorelens.Service;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.Event;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.EventMapper;
import com.scorelens.Repository.EventRepo;
import com.scorelens.Repository.PlayerRepo;
import com.scorelens.Repository.RoundRepo;
import com.scorelens.Service.Interface.IEventService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventService implements IEventService {

    @Autowired
    EventRepo eventRepo;

    @Autowired
    EventMapper eventMapper;

    @Autowired
    RoundRepo roundRepo;

    @Autowired
    PlayerRepo playerRepo;


    @Override
    public EventResponse addEvent(EventRequest eventRequest) {
        if (!roundRepo.existsById(eventRequest.getRoundID()))
            throw new AppException(ErrorCode.ROUND_NOT_FOUND);
        if (!playerRepo.existsById(eventRequest.getPlayerID()))
            throw new AppException(ErrorCode.PLAYER_NOT_FOUND);
        Event event = eventMapper.toEventRequest(eventRequest);
        //set timestamp
        event.setTimeStamp(LocalDateTime.now());
        eventRepo.save(event);
        return eventMapper.toEventResponse(event);
    }

    @Override
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepo.findAll();
        if (events.isEmpty())
            throw new AppException(ErrorCode.NULL_EVENT);
        return eventMapper.toEventResponses(events);
    }


    @Override
    public List<EventResponse> getEventsByPlayerID(int playerID) {
        List<Event> events = eventRepo.findAllByPlayer_PlayerID(playerID);
        if (events.isEmpty())
            throw new AppException(ErrorCode.NULL_EVENT_PLAYERID);
        return eventMapper.toEventResponses(events);
    }

    @Override
    public List<EventResponse> getEventsByRoundID(int roundID) {
        List<Event> events = eventRepo.findAllByRound_RoundID(roundID);
        if (events.isEmpty())
            throw new AppException(ErrorCode.NULL_EVENT_ROUNDID);
        return eventMapper.toEventResponses(events);
    }

    @Override
    public boolean deleteEventByPlayerID(int playerID) {
        List<Event> events = eventRepo.findAllByPlayer_PlayerID(playerID);
        if (events.isEmpty())
            throw new AppException(ErrorCode.NULL_EVENT_PLAYERID);
        eventRepo.deleteAll(events);
        return true;
    }

    @Override
    public boolean deleteEventByRoundID(int roundID) {
        List<Event> events = eventRepo.findAllByRound_RoundID(roundID);
        if (events.isEmpty())
            throw new AppException(ErrorCode.NULL_EVENT_ROUNDID);
        eventRepo.deleteAll(events);
        return true;
    }

    @Override
    public List<EventResponse> getEventsByPlayerIDAndRoundID(int playerID, int roundID) {
        List<Event> list = eventRepo.findAllByRound_RoundIDAndPlayer_PlayerID(roundID, playerID);
        if (list.isEmpty())
            throw new AppException(ErrorCode.EMPTY_LIST);
        return eventMapper.toEventResponses(list);
    }
}
