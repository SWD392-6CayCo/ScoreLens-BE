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
    EMPTY_LIST(1006, "Empty list"),
    INVALID_STATUS(1007, "Status must be active or inactive"),
    // USER PASSWORD
    NOT_MATCH_PASSWORD(1009, "Password do not match"),
    DUPLICATED_PASSWORD(1011, "New password is duplicated to old password"),
    //Authentication
    UNAUTHENTICATED(1013, "Unauthenticated"),
    UNSUPPORTED_USER_TYPE(1014, "Unsupported User Type"),
    INCORRECT_EMAIL_OR_PASSWORD(1015, "Incorrect email or password"),
    //-------------------------- STORE -----------------------------------
    STORE_EXIST(1001, "This store's name is already existed"),
    STORE_NOT_FOUND(1005, "Store Not Found")
    //-------------------------- BILLIARD STORE -----------------------------------

    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
