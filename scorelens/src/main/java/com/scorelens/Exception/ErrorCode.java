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
    MANAGER_NOT_EXIST(1100, "Not found manager manage staff", HttpStatus.NOT_FOUND),
    STAFF_NOT_EXIST(1101, "Staff not found", HttpStatus.NOT_FOUND),
    // USER PASSWORD
    NOT_MATCH_PASSWORD(1006, "Password do not match", HttpStatus.BAD_REQUEST),
    DUPLICATED_PASSWORD(1007, "New password is duplicated to old password", HttpStatus.CONFLICT),
    PASSWORD_LENGTH(999, "Password length must be at least {min} characters", HttpStatus.BAD_REQUEST),
    //Authentication
    UNAUTHENTICATED(1008, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZE(1099, "You do not have permission", HttpStatus.FORBIDDEN),
    UNSUPPORTED_USER_TYPE(1009, "Unsupported User Type", HttpStatus.BAD_REQUEST),
    INCORRECT_EMAIL_OR_PASSWORD(1010, "Incorrect email or password", HttpStatus.BAD_REQUEST),
    USER_INACTIVE(1011, "This account is inactive", HttpStatus.FORBIDDEN),

    //-------------------------- STORE -----------------------------------
    STORE_EXIST(1012, "This store's name is already existed", HttpStatus.CONFLICT),
    STORE_NOT_FOUND(1013, "Store Not Found", HttpStatus.NOT_FOUND),
    //-------------------------- BILLIARD STORE -----------------------------------
    TABLE_NOT_FOUND(1014, "Table not found", HttpStatus.NOT_FOUND),
    TABLE_NOT_AVAILABLE(1014, "Table not available", HttpStatus.BAD_REQUEST),
    //-------------------------- BILLIARD MATCH -----------------------------------
    MATCH_NOT_FOUND(1015,"Match not found", HttpStatus.NOT_FOUND),
    MATCH_COMPLETED(1015,"Match completed", HttpStatus.BAD_REQUEST),
    //-------------------------- ROUND  -----------------------------------
    ROUND_NOT_FOUND(1016, "Round not found", HttpStatus.NOT_FOUND),
    SET_NOT_FOUND(1016, "GameSet not found", HttpStatus.NOT_FOUND),
    CREATE_TABLE_FAILED(1005, "Create table failed",HttpStatus.BAD_REQUEST),

    //-------------------------- AWS S3 -----------------------------------
    DELETE_FILE_FAILED(1005, "Delete file from s3 failed",HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(1026, "File upload failed", HttpStatus.BAD_REQUEST),
    FILE_DELETE_FAILED(1027, "File delete failed", HttpStatus.BAD_REQUEST),
    FILE_EMPTY(1028, "File is empty", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(1029, "File size exceeds maximum limit (5MB)", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1030, "Invalid file type. Only images are allowed", HttpStatus.BAD_REQUEST),

    //-------------------------- PLAYER  -----------------------------------
    PLAYER_NOT_FOUND(1017, "Player not found", HttpStatus.NOT_FOUND),
    PLAYER_SAVED(1017, "Player was saved as the other customer", HttpStatus.BAD_REQUEST),
    //-------------------------- EVENT  -----------------------------------
    NULL_EVENT(1018, "Event is null", HttpStatus.BAD_REQUEST),
    NULL_EVENT_PLAYERID(1019, "No data matched with this player", HttpStatus.BAD_REQUEST),
    NULL_EVENT_GAMESETID(1020, "No data matched with this game set", HttpStatus.BAD_REQUEST),
    EVENT_NOT_FOUND(1021, "Event not found", HttpStatus.NOT_FOUND),

    //-------------------------- ROUND  -----------------------------------
    MODE_NOT_FOUND(1022, "Mode not found", HttpStatus.NOT_FOUND),

    //-------------------------- ROUND  -----------------------------------
    TEAM_NOT_FOUND(1023, "Team not found", HttpStatus.NOT_FOUND),

    TEAM_NOT_NULL(1023, "Team set up requires a list of teams", HttpStatus.BAD_REQUEST),
    ALL_NOT_NULL(1025, "Either staffID or customerID is not null", HttpStatus.BAD_REQUEST),
    ALL_NOT_VALUE(1025, "Either staffID or customerID is not null", HttpStatus.BAD_REQUEST),

    KAFKA_SEND_FAILED(1005, "Send kafka message failed", HttpStatus.BAD_REQUEST),
    MIN_SCORE(1026, "Score is zero. Can not minus!", HttpStatus.BAD_REQUEST),

    //-------------------------- EMAIL -----------------------------------
    EMAIL_SEND_FAILED(1027, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_MISMATCH(1028, "Password and confirm password do not match", HttpStatus.BAD_REQUEST),
    INVALID_RESET_TOKEN(1029, "Invalid or expired reset token", HttpStatus.BAD_REQUEST),

    //-------------------------- REDIS -----------------------------------
    REDIS_CONNECTION_FAILED(1031, "Unable to connect to Redis", HttpStatus.INTERNAL_SERVER_ERROR)
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
