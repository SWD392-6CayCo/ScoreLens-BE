package com.scorelens.Security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenCookieManager {
    
    private boolean isIOSSafari(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return false;
        
        return userAgent.contains("iPhone") || userAgent.contains("iPad") || 
               (userAgent.contains("Safari") && userAgent.contains("Mobile"));
    }
    
    public void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken){
        addAuthCookies(response, accessToken, refreshToken, null);
    }
    
    public void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken, HttpServletRequest request){
        // Detect iOS Safari for different SameSite policy
        String sameSitePolicy = "Lax"; // Default safe option
        
        if (request != null && isIOSSafari(request)) {
            sameSitePolicy = "Strict"; // Most restrictive but works on iOS
            log.info("Detected iOS Safari, using SameSite=Strict");
        }
        
        Cookie accessTokenCookie = new Cookie("AccessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60*60);
        accessTokenCookie.setAttribute("SameSite", sameSitePolicy);

        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60*60*24*7);
        refreshTokenCookie.setAttribute("SameSite", sameSitePolicy);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
    
    public void clearAuthCookies(HttpServletResponse response){
        clearAuthCookies(response, null);
    }
    
    public void clearAuthCookies(HttpServletResponse response, HttpServletRequest request){
        String sameSitePolicy = "Lax";
        
        if (request != null && isIOSSafari(request)) {
            sameSitePolicy = "Strict";
        }
        
        Cookie accessTokenCookie = new Cookie("AccessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setAttribute("SameSite", sameSitePolicy);

        Cookie refreshTokenCookie = new Cookie("RefreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setAttribute("SameSite", sameSitePolicy);

        // QUAN TRỌNG: Phải add cookies vào response
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        log.info("Cleared authentication cookies: AccessToken and RefreshToken");
    }
}
