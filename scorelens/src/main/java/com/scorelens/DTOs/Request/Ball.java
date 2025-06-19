package com.scorelens.DTOs.Request;

import lombok.Data;

import java.util.List;

@Data
public class Ball {
    private List<Integer> start;
    private List<Integer> end;
    private boolean potted;
}
