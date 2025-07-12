package org.highfive.backend.auth.repository.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

class TokenRedisRepositoryTest {

    private final String USER_ID = "user_id";
    private final String TOKEN = "token";
    private final String STORED_TOKEN = "stored_token";
    private final String OTHER_TOKEN = "other_token";
    private final String LOGOUT = "logout";

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenRedisRepository tokenRedisRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tokenRedisRepository, "refreshTokenExpiration", 1000L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("리프레시 토큰 저장 및 조회")
    void save_and_get_refresh_token() {
        tokenRedisRepository.save(USER_ID, TOKEN);
        verify(valueOperations).set(eq(USER_ID), eq(TOKEN), eq(1000L), eq(TimeUnit.MILLISECONDS));

        when(valueOperations.get(USER_ID)).thenReturn(TOKEN);
        assertThat(tokenRedisRepository.getRefreshToken(USER_ID)).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    void delete_refresh_token() {
        tokenRedisRepository.delete(USER_ID);
        verify(redisTemplate).delete(USER_ID);
    }

    @Test
    @DisplayName("로그아웃 토큰 저장 및 블랙리스트 확인")
    void save_logout_token_and_check_blacklist() {
        final long ttl = 5000L;
        tokenRedisRepository.saveLogoutToken(TOKEN, ttl);
        verify(valueOperations).set(eq(TOKEN), eq(LOGOUT), eq(ttl), eq(TimeUnit.MILLISECONDS));

        when(redisTemplate.hasKey(TOKEN)).thenReturn(true);
        assertThat(tokenRedisRepository.isBlackListToken(TOKEN)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 유효성 - 일치")
    void is_refresh_token_valid_true() {

        when(valueOperations.get(USER_ID)).thenReturn(TOKEN);
        assertThat(tokenRedisRepository.isRefreshTokenValid(USER_ID, TOKEN)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 유효성 - 불일치")
    void is_refresh_token_valid_false() {

        when(valueOperations.get(USER_ID)).thenReturn(STORED_TOKEN);
        assertThat(tokenRedisRepository.isRefreshTokenValid(USER_ID, OTHER_TOKEN)).isFalse();
    }
}