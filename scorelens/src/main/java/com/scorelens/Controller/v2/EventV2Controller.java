package com.scorelens.Controller.v2;

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
@RequestMapping("v2/events")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventV2Controller {

    @Autowired
    EventService eventService;

    @GetMapping("")
    ResponseObject getEventsByRoundAndPlayer(@RequestParam int roundID, @RequestParam int playerID) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event list")
                .data(eventService.getEventsByPlayerIDAndRoundID(roundID, playerID))
                .build();
    }


}
