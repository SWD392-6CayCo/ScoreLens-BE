package com.scorelens.Exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MultiAppException extends Exception {
    List<AppException> appExceptions;

    public MultiAppException(List<AppException> appExceptions) {
        this.appExceptions = appExceptions;
    }
}