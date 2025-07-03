package com.scorelens.Controller.v1;

import com.scorelens.DTOs.Request.*;
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

    @PostMapping
    public ResponseObject createPlayer(@RequestBody PlayerCreateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Create new Player successfully")
                .data(playerService.createPlayer(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updatePlayer(@PathVariable Integer id, @RequestBody PlayerUpdateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update Player information successfully")
                .data(playerService.updatePlayer(id, request))
                .build();
    }

    @PutMapping("/save/{id}")
    public ResponseObject saveCustomer(@PathVariable Integer id, @RequestBody CustomerSaveRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update Player information successfully")
                .data(playerService.updateCustomer(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deletePlayer(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Player with ID " + id + " has been deleted")
                .data(playerService.delete(id))
                .build();
    }
}
