package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Team;
import com.scorelens.Enums.ResultStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

///**
// * DTO for {@link com.scorelens.Entity.Player}
// */
@Getter
@Setter
public class PlayerResponse {
    private Integer teamID;
    private int playerID;
    private String name;
    private int totalScore;
    private String customerID;
    private ResultStatus status; //win, lose, draw, pending
    private LocalDateTime createAt;
}