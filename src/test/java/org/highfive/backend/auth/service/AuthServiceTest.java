package org.highfive.backend.auth.service;

import org.highfive.backend.auth.client.KakaoOAuthClient;
import org.highfive.backend.auth.client.dto.response.KakaoTokenResponseDto;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private KakaoOAuthClient kakaoOAuthClient;
    @Mock private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("OAuth 로그인 성공 - 새로운 사용자 저장")
    void login_success() {
        // given
        OAuthRequestDto requestDto = new OAuthRequestDto("auth-code");

        KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
                "mock-access-token",
                "Bearer",
                1234,
                "mock-refresh-token",
                5678,
                "profile"
        );

        KakaoUserResponseDto.KakaoProfile profile = new KakaoUserResponseDto.KakaoProfile("test-nickname", "http://image.url");
        KakaoUserResponseDto.KakaoAccount account = new KakaoUserResponseDto.KakaoAccount(profile);
        KakaoUserResponseDto userResponse = new KakaoUserResponseDto("kakao-user-id", account);

        when(kakaoOAuthClient.requestToken("auth-code")).thenReturn(tokenResponse);
        when(kakaoOAuthClient.requestUser("mock-access-token")).thenReturn(userResponse);
        when(userRepository.existsByKakaoUserId("kakao-user-id")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // 그대로 반환

        // when
        Response<?> response = authService.login(requestDto);

        // then
        assertThat(response.code()).isEqualTo(SuccessCode.OK.getCode());
        assertThat(response.content()).isEqualTo("test-nickname");
        assertThat(response.message()).isEqualTo(SuccessCode.OK.getMessage());

        verify(userRepository).save(any(User.class));
    }
}