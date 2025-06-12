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
    ResponseObject getEvent(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event found")
                .data(eventService.getEventsByPlayerID(id))
                .build();
    }

    @GetMapping("/round/{id}")
    ResponseObject geEvent(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event found")
                .data(eventService.getEventsByRoundID(id))
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

    @DeleteMapping("/round/{id}")
    ResponseObject deleteEventByRoundID(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Events deleted")
                .data(eventService.deleteEventByRoundID(id))
                .build();
    }

    @GetMapping("/v2")
    ResponseObject getEventsByRoundAndPlayer(@RequestParam int roundID, @RequestParam int playerID) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event list")
                .data(eventService.getEventsByPlayerIDAndRoundID(roundID, playerID))
                .build();
    }


}
