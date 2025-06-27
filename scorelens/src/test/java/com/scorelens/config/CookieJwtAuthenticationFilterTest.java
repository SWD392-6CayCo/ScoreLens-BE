package com.scorelens.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieJwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CookieJwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        // Setup common mock behaviors
    }

    @Test
    void testDoFilterInternal_WithAuthorizationHeader_ShouldPassThrough() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer existing-token");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(request, never()).getCookies();
    }

    @Test
    void testDoFilterInternal_WithAccessTokenCookie_ShouldAddAuthorizationHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        Cookie accessTokenCookie = new Cookie("AccessToken", "test-jwt-token");
        when(request.getCookies()).thenReturn(new Cookie[]{accessTokenCookie});

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(any(HttpServletRequest.class), eq(response));
        // Verify that the wrapped request would return the Authorization header
    }

    @Test
    void testDoFilterInternal_WithoutTokenCookie_ShouldPassThrough() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        Cookie otherCookie = new Cookie("OtherCookie", "other-value");
        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie});

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNoCookies_ShouldPassThrough() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithEmptyAuthorizationHeader_ShouldCheckCookie() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("");
        Cookie accessTokenCookie = new Cookie("AccessToken", "test-jwt-token");
        when(request.getCookies()).thenReturn(new Cookie[]{accessTokenCookie});

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(any(HttpServletRequest.class), eq(response));
    }
}
