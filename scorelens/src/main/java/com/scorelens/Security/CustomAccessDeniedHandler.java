package com.scorelens.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        ErrorCode errorCode = ErrorCode.UNAUTHORIZE;
        
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ResponseObject responseObject = ResponseObject.builder()
                .status(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
        
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseObject));
        
        log.warn("Access denied for request: {} - {}", request.getMethod(), request.getRequestURI());
    }
}
