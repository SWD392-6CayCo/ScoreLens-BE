package com.scorelens.Controller.v1;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Event", description = "Handling match event")
@RestController
@RequestMapping("v1/events")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventV1Controller {

    @Autowired
    EventService eventService;

    @PostMapping
    ResponseObject addEvent(@RequestBody EventRequest event) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event added")
                .data(eventService.addEvent(event))
                .build();
    }

    @GetMapping
    ResponseObject getEvents() {
        return ResponseObject.builder()
                .data(1000)
                .message("Event list")
                .data(eventService.getAllEvents())
                .build();
    }

    @GetMapping("/player/{id}")
    ResponseObject getEventPlayer(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event found")
                .data(eventService.getEventsByPlayerID(id))
                .build();
    }

    @GetMapping("/game_set/{id}")
    ResponseObject getEventGameSet(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event found")
                .data(eventService.getEventsByGameSetID(id))
                .build();
    }

    @DeleteMapping("/player/{id}")
    ResponseObject deleteEventByPlayerID(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Events deleted")
                .data(eventService.deleteEventByPlayerID(id))
                .build();
    }

    @DeleteMapping("/game_set/{id}")
    ResponseObject deleteEventByRoundID(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Events deleted")
                .data(eventService.deleteEventByGameSetID(id))
                .build();
    }

    @GetMapping("/game_set/count/{gameSetID}")
    ResponseObject countEventByGameSetID(@PathVariable int gameSetID) {
        return ResponseObject.builder()
                .status(1000)
                .message("Events count")
                .data(eventService.countEventsGameSetID(gameSetID))
                .build();
    }


}
