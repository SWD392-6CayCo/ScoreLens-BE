package com.scorelens.DTOs.Request;

import com.scorelens.Enums.KafkaCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationRequest {

    private KafkaCode code;
    private String tableID;
    private Integer modeID;
    private Information data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Information {
        private String cameraUrl;
        private Integer totalSet;
        private List<GameSet> sets;
        private List<Team> teams;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GameSet{
        private int gameSetID;
        private int raceTo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Team{
        private int teamID;
        private List<Player> players;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Player{
        private int playerID;
        private String name;
    }



}
