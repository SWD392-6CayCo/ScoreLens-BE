package com.scorelens.Exception;


import com.scorelens.Constants.ValidationMessages;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import com.scorelens.Exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIES_EXCEPTION(9999, "Uncategories exception", HttpStatus.INTERNAL_SERVER_ERROR),
    //-------------------------- USER -----------------------------------
    EMAIL_EXSITED(1001, "This email is already in use", HttpStatus.CONFLICT),
    PHONE_EXISTED(1002, "Phone number is already in use", HttpStatus.CONFLICT),
    USER_NOT_EXIST(1003, "User Not Found", HttpStatus.NOT_FOUND),
    EMPTY_LIST(1004, "Empty list", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1005, "Status must be active or inactive", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1098, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    NAME_LENGTH(1097, "Name must be at least {min}", HttpStatus.BAD_REQUEST),
    // USER PASSWORD
    NOT_MATCH_PASSWORD(1006, "Password do not match", HttpStatus.BAD_REQUEST),
    DUPLICATED_PASSWORD(1007, "New password is duplicated to old password", HttpStatus.CONFLICT),
    PASSWORD_LENGTH(999, "Password length must be at least {min} characters", HttpStatus.BAD_REQUEST),
    //Authentication
    UNAUTHENTICATED(1008, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZE(1099, "You do not have permission", HttpStatus.FORBIDDEN),
    UNSUPPORTED_USER_TYPE(1009, "Unsupported User Type", HttpStatus.BAD_REQUEST),
    INCORRECT_EMAIL_OR_PASSWORD(1010, "Incorrect email or password", HttpStatus.UNAUTHORIZED),
    USER_INACTIVE(1011, "This account is inactive", HttpStatus.FORBIDDEN),

    //-------------------------- STORE -----------------------------------
    STORE_EXIST(1012, "This store's name is already existed", HttpStatus.CONFLICT),
    STORE_NOT_FOUND(1013, "Store Not Found", HttpStatus.NOT_FOUND),
    //-------------------------- BILLIARD STORE -----------------------------------
    TABLE_NOT_FOUND(1014, "Table not found", HttpStatus.NOT_FOUND),

    //-------------------------- BILLIARD MATCH -----------------------------------
    MATCH_NOT_FOUND(1015,"Match not found", HttpStatus.NOT_FOUND),
    //-------------------------- ROUND  -----------------------------------
    ROUND_NOT_FOUND(1016, "Round not found", HttpStatus.NOT_FOUND),
    SET_NOT_FOUND(1016, "GameSet not found", HttpStatus.NOT_FOUND),


    //-------------------------- PLAYER  -----------------------------------
    PLAYER_NOT_FOUND(1017, "Player not found", HttpStatus.NOT_FOUND),

    //-------------------------- EVENT  -----------------------------------
    NULL_EVENT(1018, "Event is null", HttpStatus.BAD_REQUEST),
    NULL_EVENT_PLAYERID(1019, "No data matched with this player", HttpStatus.BAD_REQUEST),
    NULL_EVENT_ROUNDID(1020, "No data matched with this round", HttpStatus.BAD_REQUEST),
    EVENT_NOT_FOUND(1021, "Event not found", HttpStatus.NOT_FOUND),

    //-------------------------- ROUND  -----------------------------------
    MODE_NOT_FOUND(1022, "Mode not found", HttpStatus.NOT_FOUND),

    //-------------------------- ROUND  -----------------------------------
    TEAM_NOT_FOUND(1023, "Team not found", HttpStatus.NOT_FOUND),

    TEAM_NOT_NULL(1023, "Team set up requires a list of teams", HttpStatus.BAD_REQUEST),
    ALL_NOT_NULL(1025, "Either staffID or customerID is not null", HttpStatus.BAD_REQUEST),
    ALL_NOT_VALUE(1025, "Either staffID or customerID is not null", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private HttpStatusCode statusCode;

}
