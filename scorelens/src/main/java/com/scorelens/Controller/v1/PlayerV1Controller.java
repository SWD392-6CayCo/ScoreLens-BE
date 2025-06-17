package com.scorelens.Controller.v1;

import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.PlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player", description = "Player APIs")
@RestController
@RequestMapping("v1/players")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerV1Controller {

    PlayerService playerService;

    @GetMapping()
    public ResponseObject getAllPlayers() {
        var players = playerService.getAllPlayers();
        return ResponseObject.builder()
                .status(1000)
                .message("Get player list")
                .data(players)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject getPlayerById(@PathVariable int id) {
        var player = playerService.getPlayerById(id);
        return ResponseObject.builder()
                .status(1000)
                .message("Get player successfully")
                .data(player)
                .build();
    }
}
