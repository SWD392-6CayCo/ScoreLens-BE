package com.scorelens.Exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    //-------------------------- USER -----------------------------------
    EMAIL_EXSITED(1001, "This email is already in use"),
    PHONE_EXISTED(1003, "Phone number is already in use"),;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
