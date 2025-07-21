package com.scorelens.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String INVALIDATED_TOKEN_PREFIX = "invalidated_token:";
    private static final String RESET_TOKEN_PREFIX = "reset_token:";
    private static final String USER_SESSION_PREFIX = "user_session:";

    /**
     * Thêm token vào blacklist (thay thế table invalidtoken)
     */
    public void invalidateToken(String tokenId, Date expiryTime) {
        String key = INVALIDATED_TOKEN_PREFIX + tokenId;
        
        // Tính thời gian TTL dựa trên expiry time của token
        long ttlSeconds = (expiryTime.getTime() - System.currentTimeMillis()) / 1000;
        
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, "invalidated", ttlSeconds, TimeUnit.SECONDS);
            log.info("Token {} invalidated in Redis with TTL {} seconds", tokenId, ttlSeconds);
        } else {
            log.warn("Token {} already expired, not adding to Redis", tokenId);
        }
    }

    /**
     * Kiểm tra token có bị invalidate không
     */
    public boolean isTokenInvalidated(String tokenId) {
        String key = INVALIDATED_TOKEN_PREFIX + tokenId;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Lưu reset password token (thay thế JWT với database)
     */
    public void saveResetToken(String email, String resetToken) {
        String key = RESET_TOKEN_PREFIX + email;
        
        // Reset token có thời hạn 5 phút -> sau đó tự động xóa
        redisTemplate.opsForValue().set(key, resetToken, Duration.ofMinutes(5));
        log.info("Reset token saved for email: {}", email);
    }

    /**
     * Lấy và xóa reset token (one-time use)
     */
    public String getAndDeleteResetToken(String email) {
        String key = RESET_TOKEN_PREFIX + email;
        String token = (String) redisTemplate.opsForValue().get(key);
        
        if (token != null) {
            redisTemplate.delete(key); //xóa ngay sau khi lấy
            log.info("Reset token retrieved and deleted for email: {}", email);
        }
        
        return token;
    }

    /**
     * Lưu thông tin session user (cache user info)
     */
    public void saveUserSession(String userId, Object userInfo, Duration ttl) {
        String key = USER_SESSION_PREFIX + userId;
        redisTemplate.opsForValue().set(key, userInfo, ttl);
        log.info("User session saved for userId: {}", userId);
    }

    /**
     * Lấy thông tin session user
     */
    public Object getUserSession(String userId) {
        String key = USER_SESSION_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Xóa session user (khi logout)
     */
    public void deleteUserSession(String userId) {
        String key = USER_SESSION_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("User session deleted for userId: {}", userId);
    }

    /**
     * Xóa tất cả token invalidated đã hết hạn (cleanup job)
     */
    public void cleanupExpiredTokens() {
        Set<String> keys = redisTemplate.keys(INVALIDATED_TOKEN_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            log.info("Found {} invalidated token keys for cleanup check", keys.size());
            // Redis tự động xóa key hết hạn, không cần manual cleanup
        }
    }

    /**
     * Lấy thống kê Redis
     */
    public long getInvalidatedTokenCount() {
        Set<String> keys = redisTemplate.keys(INVALIDATED_TOKEN_PREFIX + "*");
        return keys != null ? keys.size() : 0;
    }

    public long getActiveSessionCount() {
        Set<String> keys = redisTemplate.keys(USER_SESSION_PREFIX + "*");
        return keys != null ? keys.size() : 0;
    }
}
