package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Team;
import com.scorelens.Enums.ResultStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.scorelens.Entity.Player}
 */
@AllArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerResponse implements Serializable {
    int playerID;
    String name;
    int totalScore;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate createAt;
    ResultStatus status; //win, lose, draw, pending
    TeamResponse team;
    CustomerResponseDto customer;
}