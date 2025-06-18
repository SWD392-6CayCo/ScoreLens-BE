package com.scorelens.DTOs.Request;

import lombok.Data;

@Data
public class Collision {
    private int ball1;
    private int ball2;
    private double time;
}
