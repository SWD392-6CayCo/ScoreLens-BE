package com.scorelens.DTOs.Request;

import com.scorelens.Enums.ResultStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerCreateRequest {
    String name;
//    int totalScore;
    LocalDate createAt;
    ResultStatus status;
    String teamID;
    String customerID; //có thể null
}
