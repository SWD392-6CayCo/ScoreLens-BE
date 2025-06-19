package com.scorelens.DTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BilliardMatchCreateRequest {
    private String billiardTableID;
    private Integer modeID;
    private String staffID;
    private String customerID;
    private String setUp;
    private Integer totalSet;
    private Integer raceTo;

    // Use only for "custom"
    private List<TeamConfig> teamConfigs;

    @Getter
    @Setter
    public static class TeamConfig {
        private String name;
        private Integer totalMember;
        private List<String> memberNames;
    }
}
