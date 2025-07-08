package org.highfive.backend.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenRepository {

    private final String LOG_OUT = "logout";

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refreshToken-expiration}")
    private Long refreshTokenExpiration;

    public void save(final String userId, final String refreshToken) {
        redisTemplate.opsForValue().set(String.valueOf(userId), refreshToken, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public void saveLogoutToken(final String accessToken, final long tokenRemainingTime) {
        redisTemplate.opsForValue().set(accessToken, LOG_OUT, tokenRemainingTime, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(final String userId) {
        return redisTemplate.opsForValue().get(String.valueOf(userId));
    }

    public void delete(final String userId) {
        redisTemplate.delete(String.valueOf(userId));
    }

    public boolean isBlackListToken(final String token) {
        return redisTemplate.hasKey(token);
    }

    public boolean isRefreshTokenValid(final String userId, final String refreshToken) {
        final String savedToken = getRefreshToken(userId);
        return savedToken != null && refreshToken.equals(savedToken);
    }
}
