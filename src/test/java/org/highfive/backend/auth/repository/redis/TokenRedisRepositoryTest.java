package org.highfive.backend.auth.repository.redis;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenRedisRepositoryTest {

    @Autowired
    private TokenRedisRepository tokenRedisRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String USER_ID = "user_id";
    private final String TOKEN = "token";
    private final String OTHER_TOKEN = "other_token";

    @BeforeEach
    void flushAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("리프레시 토큰 저장 및 조회")
    void save_and_get_refresh_token() {
        tokenRedisRepository.save(USER_ID, TOKEN);
        String value = tokenRedisRepository.getRefreshToken(USER_ID);
        assertThat(value).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    void delete_refresh_token() {
        tokenRedisRepository.save(USER_ID, TOKEN);
        tokenRedisRepository.delete(USER_ID);
        assertThat(tokenRedisRepository.getRefreshToken(USER_ID)).isNull();
    }

    @Test
    @DisplayName("로그아웃 토큰 저장 및 블랙리스트 확인")
    void save_logout_token_and_check_blacklist() {
        tokenRedisRepository.saveLogoutToken(TOKEN, 1000L);
        assertThat(tokenRedisRepository.isBlackListToken(TOKEN)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 유효성 - 일치")
    void is_refresh_token_valid_true() {
        tokenRedisRepository.save(USER_ID, TOKEN);
        assertThat(tokenRedisRepository.isRefreshTokenValid(USER_ID, TOKEN)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 유효성 - 불일치")
    void is_refresh_token_valid_false() {
        tokenRedisRepository.save(USER_ID, TOKEN);
        assertThat(tokenRedisRepository.isRefreshTokenValid(USER_ID, OTHER_TOKEN)).isFalse();
    }
}