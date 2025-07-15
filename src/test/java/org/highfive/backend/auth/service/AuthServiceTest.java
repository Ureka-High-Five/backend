package org.highfive.backend.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.highfive.backend.auth.client.KakaoOAuthClient;
import org.highfive.backend.auth.client.dto.response.KakaoTokenResponseDto;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.dto.request.ReissueRequestDto;
import org.highfive.backend.auth.dto.response.OnboardingResponseDto;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.auth.repository.redis.TokenRedisRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Mock
    private TokenRedisRepository tokenRedisRepository;

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
        when(tokenService.generateAccessToken(KAKAO_USER_ID, List.of(user.getUserRole().name()))).thenReturn(ACCESS_TOKEN);
        when(tokenService.generateRefreshToken(KAKAO_USER_ID, List.of(user.getUserRole().name()))).thenReturn(REFRESH_TOKEN);
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

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_success() {
        // given
        User user = mockUser(UserRole.USER);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        // when
        when(tokenService.getAuthentication(REFRESH_TOKEN, TokenType.REFRESHTOKEN)).thenReturn(authentication);
        when(tokenRedisRepository.isRefreshTokenValid(KAKAO_USER_ID, REFRESH_TOKEN)).thenReturn(true);
        when(tokenService.generateAccessToken(eq(KAKAO_USER_ID), any())).thenReturn(ACCESS_TOKEN);

        Response<TokenResponseDto> response = authService.reissue(new ReissueRequestDto(REFRESH_TOKEN));

        // then
        assertThat(response.code()).isEqualTo(SuccessCode.OK.getCode());
        assertThat(response.content()).isInstanceOf(TokenResponseDto.class);

        TokenResponseDto content = response.content();
        assertThat(content.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(content.isNew()).isFalse();
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 리프레시 토큰 불일치")
    void reissue_token_mismatch() {
        // given
        final User user = mockUser(UserRole.USER);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        // when
        when(tokenService.getAuthentication(REFRESH_TOKEN, TokenType.REFRESHTOKEN)).thenReturn(authentication);
        doNothing().when(tokenService).validateToken(REFRESH_TOKEN, TokenType.REFRESHTOKEN);
        when(tokenRedisRepository.isRefreshTokenValid(KAKAO_USER_ID, REFRESH_TOKEN)).thenReturn(false);

        // then
        assertThat(((BusinessException)
                catchThrowable(() -> authService.reissue(new ReissueRequestDto(REFRESH_TOKEN))))
        ).extracting(BusinessException::getErrorCode)
                .isEqualTo(AuthErrorCode.TOKEN_MISMATCH_ERROR);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 리프레시 토큰")
    void reissue_token_invalid() {
        // given
        final User user = mockUser(UserRole.USER);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of());

        // when
        when(tokenService.getAuthentication(REFRESH_TOKEN, TokenType.REFRESHTOKEN)).thenReturn(authentication);
        doThrow(new BusinessException(AuthErrorCode.REFRESH_TOKEN_ERROR))
                .when(tokenService).validateToken(REFRESH_TOKEN, TokenType.REFRESHTOKEN);

        // then
        assertThat(((BusinessException)
                catchThrowable(() -> authService.reissue(new ReissueRequestDto(REFRESH_TOKEN))))
        ).extracting(BusinessException::getErrorCode)
                .isEqualTo(AuthErrorCode.REFRESH_TOKEN_ERROR);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() {

        // given
        final User user = mockUser(UserRole.USER);
        final HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(tokenService.resolveToken(request)).thenReturn(ACCESS_TOKEN);
        when(tokenService.getRemainingTime(ACCESS_TOKEN)).thenReturn(3L);

        // then
        Response<Void> response = authService.logout(user, request);

        assertThat(response.code()).isEqualTo(SuccessCode.OK.getCode());
        assertThat(response.content()).isNull();
        assertThat(response.message()).isEqualTo(SuccessCode.OK.getMessage());

        verify(tokenRedisRepository).saveLogoutToken(ACCESS_TOKEN, 3L);
        verify(tokenRedisRepository).delete(KAKAO_USER_ID);
    }

    @Test
    @DisplayName("로그아웃 실패 - 액세스 토큰 예외")
    void logout_fail_when_access_token_is_missing() {
        // given
        final User user = mockUser(UserRole.USER);
        final HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(tokenService.resolveToken(request)).thenReturn(null);
        doThrow(new BusinessException(AuthErrorCode.ACCESS_TOKEN_ERROR))
                .when(tokenService)
                .getRemainingTime(null);

        // then
        assertThat(((BusinessException)
                catchThrowable(() -> authService.logout(user, request)))
        ).extracting(BusinessException::getErrorCode)
                .isEqualTo(AuthErrorCode.ACCESS_TOKEN_ERROR);
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

        return user;
    }
}