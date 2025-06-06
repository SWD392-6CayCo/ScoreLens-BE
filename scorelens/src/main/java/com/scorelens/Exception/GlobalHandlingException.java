package com.scorelens.Exception;

import com.scorelens.Entity.ResponseObject;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
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
        return ResponseEntity.status(500).body(ResponseObject.builder()
                .status(500)
                .message("Internal Server Error: " + exception.getMessage())
                .build());
    }
}
