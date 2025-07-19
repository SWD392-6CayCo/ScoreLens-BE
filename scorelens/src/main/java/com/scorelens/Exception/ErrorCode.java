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

    PASSWORD_LENGTH(999, "Password length must be at least {min} characters", HttpStatus.BAD_REQUEST),
    
    //-------------------------- USER -----------------------------------
    EMAIL_EXSITED(1001, "This email is already in use", HttpStatus.CONFLICT),
    PHONE_EXISTED(1002, "Phone number is already in use", HttpStatus.CONFLICT),
    USER_NOT_EXIST(1003, "User Not Found", HttpStatus.NOT_FOUND),
    EMPTY_LIST(1004, "Empty list", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1005, "Status must be active or inactive", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1006, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    NAME_LENGTH(1007, "Name must be at least {min}", HttpStatus.BAD_REQUEST),
    MANAGER_NOT_EXIST(1008, "Not found manager manage staff", HttpStatus.NOT_FOUND),
    STAFF_NOT_EXIST(1009, "Staff not found", HttpStatus.NOT_FOUND),
    NOT_MATCH_PASSWORD(1010, "Password do not match", HttpStatus.BAD_REQUEST),
    USER_INACTIVE(1011, "This account is inactive", HttpStatus.FORBIDDEN),
    DUPLICATED_PASSWORD(1012, "New password is duplicated to old password", HttpStatus.CONFLICT),

    //-------------------------- AUTHENTICATION -----------------------------------
    UNAUTHENTICATED(1013, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZE(1014, "You do not have permission", HttpStatus.FORBIDDEN),
    UNSUPPORTED_USER_TYPE(1015, "Unsupported User Type", HttpStatus.BAD_REQUEST),
    INCORRECT_EMAIL_OR_PASSWORD(1016, "Incorrect email or password", HttpStatus.BAD_REQUEST),

    //-------------------------- STORE -----------------------------------
    STORE_EXIST(1017, "This store's name is already existed", HttpStatus.CONFLICT),
    STORE_NOT_FOUND(1018, "Store Not Found", HttpStatus.NOT_FOUND),
    
    //-------------------------- BILLIARD TABLE -----------------------------------
    TABLE_NOT_FOUND(1019, "Table not found", HttpStatus.NOT_FOUND),
    TABLE_NOT_AVAILABLE(1020, "Table not available", HttpStatus.BAD_REQUEST),
    CREATE_TABLE_FAILED(1021, "Create table failed", HttpStatus.BAD_REQUEST),
    
    //-------------------------- BILLIARD MATCH -----------------------------------
    MATCH_NOT_FOUND(1022, "Match not found", HttpStatus.NOT_FOUND),
    MATCH_COMPLETED(1023, "Match completed", HttpStatus.BAD_REQUEST),
    
    //-------------------------- ROUND & SET -----------------------------------
    ROUND_NOT_FOUND(1024, "Round not found", HttpStatus.NOT_FOUND),
    SET_NOT_FOUND(1025, "GameSet not found", HttpStatus.NOT_FOUND),

    //-------------------------- PLAYER -----------------------------------
    PLAYER_NOT_FOUND(1026, "Player not found", HttpStatus.NOT_FOUND),
    PLAYER_SAVED(1027, "Player was saved as the other customer", HttpStatus.BAD_REQUEST),
    CUSTOMER_SAVED(1028, "Customer was saved as one of the player in this match", HttpStatus.BAD_REQUEST),
    
    //-------------------------- EVENT -----------------------------------
    NULL_EVENT(1029, "Event is null", HttpStatus.BAD_REQUEST),
    NULL_EVENT_PLAYERID(1030, "No data matched with this player", HttpStatus.BAD_REQUEST),
    NULL_EVENT_GAMESETID(1031, "No data matched with this game set", HttpStatus.BAD_REQUEST),
    EVENT_NOT_FOUND(1032, "Event not found", HttpStatus.NOT_FOUND),

    //-------------------------- MODE -----------------------------------
    MODE_NOT_FOUND(1033, "Mode not found", HttpStatus.NOT_FOUND),

    //-------------------------- TEAM -----------------------------------
    TEAM_NOT_FOUND(1034, "Team not found", HttpStatus.NOT_FOUND),
    TEAM_NOT_NULL(1035, "Team set up requires a list of teams", HttpStatus.BAD_REQUEST),
    
    //-------------------------- VALIDATION -----------------------------------
    ALL_NOT_NULL(1036, "Either staffID or customerID is not null", HttpStatus.BAD_REQUEST),
    ALL_NOT_VALUE(1037, "Either staffID or customerID is not null", HttpStatus.BAD_REQUEST),
    MIN_SCORE(1038, "Score is zero. Can not minus!", HttpStatus.BAD_REQUEST),
    
    //-------------------------- AWS S3 -----------------------------------
    FILE_UPLOAD_FAILED(1039, "File upload failed", HttpStatus.BAD_REQUEST),
    FILE_DELETE_FAILED(1040, "File delete failed", HttpStatus.BAD_REQUEST),
    DELETE_FILE_FAILED(1041, "Delete file from s3 failed", HttpStatus.BAD_REQUEST),
    FILE_EMPTY(1042, "File is empty", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(1043, "File size exceeds maximum limit (5MB)", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1044, "Invalid file type. Only images are allowed", HttpStatus.BAD_REQUEST),

    //-------------------------- EMAIL -----------------------------------
    EMAIL_SEND_FAILED(1045, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_MISMATCH(1046, "Password and confirm password do not match", HttpStatus.BAD_REQUEST),
    INVALID_RESET_TOKEN(1047, "Invalid or expired reset token", HttpStatus.BAD_REQUEST),

    //-------------------------- REDIS -----------------------------------
    REDIS_CONNECTION_FAILED(1048, "Unable to connect to Redis", HttpStatus.INTERNAL_SERVER_ERROR),
    
    //-------------------------- KAFKA -----------------------------------
    KAFKA_SEND_FAILED(1049, "Send kafka message failed", HttpStatus.BAD_REQUEST),

    //-------------------------- GENERAL -----------------------------------
    UNCATEGORIES_EXCEPTION(9999, "Uncategories exception", HttpStatus.INTERNAL_SERVER_ERROR)
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
