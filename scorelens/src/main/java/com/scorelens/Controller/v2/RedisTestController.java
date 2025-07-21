package com.scorelens.Controller.v2;

import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.RedisTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Redis Test", description = "Redis connection and functionality testing")
@RequestMapping("/v2/redis")
@RequiredArgsConstructor
@Slf4j
public class RedisTestController {

    private final RedisTokenService redisTokenService;

    @GetMapping("/test")
    @Operation(summary = "Test Redis Connection", description = "Test Redis connection by saving and retrieving a token")
    public ResponseObject testRedis() {
        try {
            // Test Redis connection
            redisTokenService.saveResetToken("test@example.com", "test-token-123");
            
            // Try to retrieve it
            String retrievedToken = redisTokenService.getAndDeleteResetToken("test@example.com");
            
            if ("test-token-123".equals(retrievedToken)) {
                return ResponseObject.builder()
                        .status(1000)
                        .message("Redis connection successful!")
                        .data("Token saved and retrieved successfully")
                        .build();
            } else {
                return ResponseObject.builder()
                        .status(1001)
                        .message("Redis test failed - token mismatch")
                        .data(null)
                        .build();
            }
        } catch (Exception e) {
            log.error("Redis test failed: {}", e.getMessage(), e);
            return ResponseObject.builder()
                    .status(1002)
                    .message("Redis connection failed: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get Redis Statistics", description = "Get Redis usage statistics including token counts")
    public ResponseObject getRedisStats() {
        try {
            long invalidatedTokens = redisTokenService.getInvalidatedTokenCount();
            long activeSessions = redisTokenService.getActiveSessionCount();
            
            return ResponseObject.builder()
                    .status(1000)
                    .message("Redis stats retrieved successfully")
                    .data(new Object() {
                        public final long invalidatedTokenCount = invalidatedTokens;
                        public final long activeSessionCount = activeSessions;
                    })
                    .build();
        } catch (Exception e) {
            log.error("Failed to get Redis stats: {}", e.getMessage(), e);
            return ResponseObject.builder()
                    .status(1002)
                    .message("Failed to get Redis stats: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
