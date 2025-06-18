package com.scorelens.DTOs.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShotEvent {
    private LocalTime time;
    private String shot;
    private String player;
    private String result;
}
