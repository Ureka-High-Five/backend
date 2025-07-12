package org.highfive.backend.auth.service;

import org.highfive.backend.auth.client.KakaoOAuthClient;
import org.highfive.backend.auth.client.dto.response.KakaoTokenResponseDto;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.dto.response.OnboardingResponseDto;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private KakaoOAuthClient kakaoOAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private final String CODE = "auth-code";
    private final String KAKAO_USER_ID = "kakao-id";
    private final String NICKNAME = "박상윤";
    private final String ACCESS_TOKEN = "access-token";
    private final String REFRESH_TOKEN = "refresh-token";
    private final OAuthRequestDto oAuthRequestDto = new OAuthRequestDto(CODE);

    @Test
    @DisplayName("로그인 성공 - 이미 회원 가입을 한 유저인 경우")
    void login_success_existed_user() {
        // given
        User user = mockUser(UserRole.USER);
        mockToken();
        mockKakaoUser();
        when(userRepository.findByKakaoUserId(KAKAO_USER_ID)).thenReturn(Optional.of(user));

        // when
        Response<?> response = authService.login(oAuthRequestDto);

        // then
        assertThat(response.content()).isInstanceOf(TokenResponseDto.class);
        assertThat(response.code()).isEqualTo(SuccessCode.OK.getCode());

        TokenResponseDto content = (TokenResponseDto) response.content();
        assertThat(content.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(content.refreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(content.isNew()).isFalse();
    }

    @Test
    @DisplayName("로그인 성공 - 처음 회원 가입을 하는 유저")
    void login_success_new_user() {
        // given
        User savedUser = mockUser(UserRole.TEMP_USER);
        mockToken();
        mockKakaoUser();
        when(userRepository.findByKakaoUserId(KAKAO_USER_ID)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        Response<?> response = authService.login(oAuthRequestDto);

        // then
        assertThat(response.content()).isInstanceOf(OnboardingResponseDto.class);
        assertThat(response.code()).isEqualTo(SuccessCode.OK.getCode());

        OnboardingResponseDto content = (OnboardingResponseDto) response.content();
        assertThat(content.userId()).isEqualTo(1L);
        assertThat(content.nickname()).isEqualTo(NICKNAME);
        assertThat(content.isNew()).isTrue();
    }

    @Test
    @DisplayName("로그인 성공 - 회원 가입을 했지만, 온보딩을 하지 않은 유저")
    void login_success_temp_user() {
        // given
        User tempUser = mockUser(UserRole.TEMP_USER);
        mockToken();
        mockKakaoUser();
        when(userRepository.findByKakaoUserId(KAKAO_USER_ID)).thenReturn(Optional.of(tempUser));

        // when
        Response<?> response = authService.login(oAuthRequestDto);

        // then
        assertThat(response.content()).isInstanceOf(OnboardingResponseDto.class);
        assertThat(response.code()).isEqualTo(SuccessCode.OK.getCode());

        OnboardingResponseDto content = (OnboardingResponseDto) response.content();
        assertThat(content.userId()).isEqualTo(1L);
        assertThat(content.nickname()).isEqualTo(NICKNAME);
        assertThat(content.isNew()).isTrue();
    }

    @Test
    @DisplayName("로그인 실패 - 카카오 토큰 요청 실패")
    void login_fail_kakaoTokenError() {

        // when
        when(kakaoOAuthClient.requestToken(CODE))
                .thenThrow(new BusinessException(AuthErrorCode.KAKAO_TOKEN_ERROR));

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(new OAuthRequestDto(CODE));
        });

        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.KAKAO_TOKEN_ERROR);
    }

    @Test
    @DisplayName("로그인 실패 - 카카오 유저 정보 요청 실패")
    void login_fail_kakaoUserInfoError() {

        // when
        when(kakaoOAuthClient.requestToken(CODE))
                .thenReturn(new KakaoTokenResponseDto(ACCESS_TOKEN, "bearer", 123, REFRESH_TOKEN, 456, "scope"));

        when(kakaoOAuthClient.requestUser(ACCESS_TOKEN))
                .thenThrow(new BusinessException(AuthErrorCode.KAKAO_USERINFO_ERROR));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(oAuthRequestDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.KAKAO_USERINFO_ERROR);
    }

    private void mockToken() {
        when(kakaoOAuthClient.requestToken(CODE)).thenReturn(
                new KakaoTokenResponseDto(ACCESS_TOKEN, "bearer", 21599, REFRESH_TOKEN, 5183999, "scope")
        );
    }

    private void mockKakaoUser() {
        KakaoUserResponseDto.KakaoProfile profile = new KakaoUserResponseDto.KakaoProfile(NICKNAME, "http://image.url");
        KakaoUserResponseDto.KakaoAccount account = new KakaoUserResponseDto.KakaoAccount(profile);
        KakaoUserResponseDto userInfo = new KakaoUserResponseDto(KAKAO_USER_ID, account);
        when(kakaoOAuthClient.requestUser(ACCESS_TOKEN)).thenReturn(userInfo);
    }

    private User mockUser(UserRole role) {
        User user = User.builder()
                .id(1L)
                .kakaoUserId(KAKAO_USER_ID)
                .userRole(role)
                .name(NICKNAME)
                .build();

        if (role == UserRole.USER) {
            List<String> roles = List.of(role.name());
            when(tokenService.generateAccessToken(KAKAO_USER_ID, roles)).thenReturn(ACCESS_TOKEN);
            when(tokenService.generateRefreshToken(KAKAO_USER_ID, roles)).thenReturn(REFRESH_TOKEN);
        }

        return user;
    }
}