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
public class PlayerCreateRequest {
    Integer teamID;
    String name;
}
