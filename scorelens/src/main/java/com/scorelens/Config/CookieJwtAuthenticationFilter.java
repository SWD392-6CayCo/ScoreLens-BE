package com.scorelens.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CookieJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_COOKIE_NAME = "AccessToken"; // Tên cookie chứa JWT token
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Kiểm tra xem đã có Authorization header chưa
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // Nếu không có Authorization header, tìm token trong cookie
            String tokenFromCookie = extractTokenFromCookie(request);
            
            if (tokenFromCookie != null) {
                // Tạo một wrapper request với Authorization header được thêm vào
                HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if (AUTHORIZATION_HEADER.equalsIgnoreCase(name)) {
                            return BEARER_PREFIX + tokenFromCookie;
                        }
                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if (AUTHORIZATION_HEADER.equalsIgnoreCase(name)) {
                            return Collections.enumeration(Collections.singletonList(BEARER_PREFIX + tokenFromCookie));
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {
                        Map<String, String> headers = new HashMap<>();
                        Enumeration<String> originalHeaders = super.getHeaderNames();
                        while (originalHeaders.hasMoreElements()) {
                            String headerName = originalHeaders.nextElement();
                            headers.put(headerName, super.getHeader(headerName));
                        }
                        headers.put(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenFromCookie);
                        return Collections.enumeration(headers.keySet());
                    }
                };
                
                filterChain.doFilter(wrappedRequest, response);
                return;
            }
        }
        
        // Nếu đã có Authorization header hoặc không có token trong cookie, tiếp tục bình thường
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    // Kiểm tra cookie không null và không rỗng
                    if (value != null && !value.trim().isEmpty()) {
                        log.debug("Found AccessToken cookie with value: {}", value.substring(0, Math.min(10, value.length())) + "...");
                        return value;
                    } else {
                        log.debug("Found AccessToken cookie but value is empty or null");
                    }
                }
            }
        }
        return null;
    }
}
