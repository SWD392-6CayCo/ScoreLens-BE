package com.scorelens.Controller.v1;

import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.Service.KafkaService.KafkaProducer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Connection Checking", description = "Heart Beat")
@RestController
@RequestMapping("v1/heartbeats")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeartBeatController {
    private final KafkaProducer kafkaProducer;
    private final KafKaHeartBeat kafkaHeartBeat;

    @PostMapping("/send")
    public ResponseEntity<String> sendHeartbeatManually(@RequestParam String tableID) {
        kafkaProducer.sendHeartbeat(tableID);
        return ResponseEntity.ok("Heartbeat sent manually.");
    }

    @PostMapping("/start")
    public ResponseEntity<String> startHeartbeat() {
        kafkaHeartBeat.start();
        return ResponseEntity.ok("Heartbeat started.");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopHeartbeat() {
        kafkaHeartBeat.stop();
        return ResponseEntity.ok("Heartbeat stopped.");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Heartbeat is " + (kafkaHeartBeat.isRunning() ? "running" : "stopped"));
    }
}
