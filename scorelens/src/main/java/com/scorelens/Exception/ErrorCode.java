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
    USER_INACTIVE(1016, "This account is inactive"),

    //-------------------------- STORE -----------------------------------
    STORE_EXIST(1001, "This store's name is already existed"),
    STORE_NOT_FOUND(1005, "Store Not Found"),
    //-------------------------- BILLIARD STORE -----------------------------------
    TABLE_NOT_FOUND(1005, "Table not found"),

    //-------------------------- BILLIARD MATCH -----------------------------------
    MATCH_NOT_FOUND(1005,"Match not found"),
    //-------------------------- ROUND  -----------------------------------
    ROUND_NOT_FOUND(1005, "Round not found"),

    //-------------------------- PLAYER  -----------------------------------
    PLAYER_NOT_FOUND(1005, "Player not found"),

    //-------------------------- EVENT  -----------------------------------
    NULL_EVENT(1005, "Event is null"),
    NULL_EVENT_PLAYERID(1005, "No data matched with this player"),
    NULL_EVENT_ROUNDID(1005, "No data matched with this round"),
    EVENT_NOT_FOUND(1005, "Event not found"),
    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
