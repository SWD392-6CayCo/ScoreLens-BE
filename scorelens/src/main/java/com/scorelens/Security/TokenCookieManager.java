package com.scorelens.Security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenCookieManager {
    public void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken){
        Cookie accessTokenCookie = new Cookie("AccessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60*60);
        accessTokenCookie.setAttribute("SameSite", "None");

        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60*60*24*7);
        refreshTokenCookie.setAttribute("SameSite", "None");

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
    public void clearAuthCookies(HttpServletResponse response){
        Cookie accessTokenCookie = new Cookie("AccessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setAttribute("SameSite", "None");

        Cookie refreshTokenCookie = new Cookie("RefreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setAttribute("SameSite", "None");

        // QUAN TRỌNG: Phải add cookies vào response
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        log.info("Cleared authentication cookies: AccessToken and RefreshToken");
    }
}
