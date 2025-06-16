package com.scorelens.DTOs.Request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KafkaMessageRequest {
    private String timestamp;
    private int cueBallId;
    private List<Ball> balls;
    private List<Collision> collisions;
    private int scoreValue;
    private boolean isFoul;
    private boolean isUncertain;
    private String message;
    private String sceneUrl;
    private int matchId;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Ball {
        private int id;
        private List<Integer> start;
        private List<Integer> end;
        private boolean potted;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Collision {
        private int ball1;
        private int ball2;
        private double time;
    }
}
