package org.highfive.backend.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refreshToken-expiration}")
    private Long refreshTokenExpiration;

    public void save(final String userId, final String refreshToken) {
        redisTemplate.opsForValue().set(String.valueOf(userId), refreshToken, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(final String userId) {
        return redisTemplate.opsForValue().get(String.valueOf(userId));
    }

    public boolean isRefreshTokenValid(final String userId, final String refreshToken) {
        final String savedToken = getRefreshToken(userId);
        return savedToken != null && refreshToken.equals(savedToken);
    }
}
