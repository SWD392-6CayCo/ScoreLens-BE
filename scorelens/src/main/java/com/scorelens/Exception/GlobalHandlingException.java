package com.scorelens.Exception;

import com.scorelens.Entity.ResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalHandlingException {//Runtime exception
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ResponseObject> HandlingRuntimeException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(1000)
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ResponseObject> HandlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(errorCode.getCode());
        responseObject.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(responseObject)
                ;
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ResponseObject> HandlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZE;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ResponseObject.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    //validation
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ResponseObject> HandlingValidation(MethodArgumentNotValidException exception) {
//        String enumKey = exception.getFieldError().getDefaultMessage();
//        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
//        return ResponseEntity.badRequest().body(ResponseObject.builder()
//                .status(errorCode.getCode())
//                .message(errorCode.getMessage())
//                .build());
//    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ResponseObject> HandlingValidation(MethodArgumentNotValidException exception) {
        String messageKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = null;

        try {
            errorCode = ErrorCode.valueOf(messageKey); // Nếu là key enum
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(400)
                    .message(messageKey) // Hiển thị luôn message gốc
                    .build());
        }

        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseObject> handleUnexpectedException(Exception exception) {
        exception.printStackTrace(); // nên dùng log.error() nếu đã cấu hình logging
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(ErrorCode.UNAUTHENTICATED.getCode());
        responseObject.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());

        return ResponseEntity.badRequest().body(responseObject);
    }
}
