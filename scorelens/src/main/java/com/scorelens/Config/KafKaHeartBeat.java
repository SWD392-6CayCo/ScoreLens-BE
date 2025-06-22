package com.scorelens.Config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@Data
public class KafKaHeartBeat {

    private volatile boolean running = true;
    private volatile Instant lastConfirmedTime = Instant.now();

    public void stop() {
        this.running = false;
    }

    public void start() {
        this.running = true;
    }

    public void updateLastConfirmedTime() {
        this.lastConfirmedTime = Instant.now();
    }

    public Duration timeSinceLastConfirm() {
        return Duration.between(lastConfirmedTime, Instant.now());
    }

}
