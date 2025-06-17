package com.scorelens.Controller.v1;

import com.scorelens.DTOs.Request.TeamCreateRequest;
import com.scorelens.DTOs.Request.TeamUpdateRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.TeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Team", description = "Manage Team")
@RestController
@RequestMapping("v1/teams")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeamV1Controller {
    @Autowired
    private TeamService teamService;

    @GetMapping("/{id}")
    public ResponseObject getById(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Get Team information succesfully")
                        .data(teamService.getById(id))
                        .build();
    }

    @GetMapping("match/{id}")
    public ResponseObject getByMatch(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Teams information succesfully")
                .data(teamService.getTeamsByMatchID(id))
                .build();
    }

    @PostMapping
    public ResponseObject createTeam(@RequestBody TeamCreateRequest request) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Create new Team successfully")
                        .data(teamService.createTeam(request))
                        .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateTeam(@PathVariable Integer id, @RequestBody TeamUpdateRequest request) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Update Team information successfully")
                        .data(teamService.updateTeam(id, request))
                        .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deleteTeam(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Team with ID " + id + " has been deleted")
                        .data(teamService.delete(id))
                        .build();
    }
}
