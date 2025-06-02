package com.scorelens.Exception;


import com.scorelens.Constants.ValidationMessages;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import com.scorelens.Exception.ErrorCode;
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    //-------------------------- USER -----------------------------------
    EMAIL_EXSITED(1001, "This email is already in use"),
    PHONE_EXISTED(1003, "Phone number is already in use"),
    USER_NOT_EXIST(1005, "User Not Found"),
    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
