package org.highfive.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.dto.request.ReissueRequestDto;
import org.highfive.backend.auth.dto.response.OnboardingResponseDto;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.auth.service.AuthService;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.exception.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 요청 성공 - 회원이 아닌 경우")
    void login_success_not_member() throws Exception {

        // given
        final OAuthRequestDto dto = new OAuthRequestDto("abc");
        final OnboardingResponseDto onboardingResponseDto = new OnboardingResponseDto(1L, "abc", true);
        Response<OnboardingResponseDto> response = new Response<>(SuccessCode.OK.getCode(), onboardingResponseDto, "로그인 성공");

        // when
        when(authService.login(any(OAuthRequestDto.class))).thenReturn((Response) response);

        // then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.content.userId").value(1))
                .andExpect(jsonPath("$.content.nickname").value("abc"))
                .andExpect(jsonPath("$.content.isNew").value(true));
    }

    @Test
    @DisplayName("로그인 요청 성공 - 회원인 경우")
    void login_success_member() throws Exception {
        // given
        final OAuthRequestDto dto = new OAuthRequestDto("abc");
        final TokenResponseDto tokenResponseDto = new TokenResponseDto("abcabce", "abcabce", true);
        final Response<TokenResponseDto> response = new Response<>(SuccessCode.OK.getCode(), tokenResponseDto, SuccessCode.OK.getMessage());

        // when
        when(authService.login(any(OAuthRequestDto.class))).thenReturn((Response) response);

        // then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()))
                .andExpect(jsonPath("$.content.accessToken").value("abcabce"))
                .andExpect(jsonPath("$.content.refreshToken").value("abcabce"));
    }

    @Test
    @DisplayName("로그인 요청 실패 - 카카오 인증 토큰 요청 오류")
    void login_fail_kakao_token_error() throws Exception {
        // given
        final OAuthRequestDto dto = new OAuthRequestDto("abc");

        // when
        when(authService.login(any(OAuthRequestDto.class)))
                .thenThrow(new BusinessException(AuthErrorCode.KAKAO_TOKEN_ERROR));

        // then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(AuthErrorCode.KAKAO_TOKEN_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.KAKAO_TOKEN_ERROR.getMessage()));
    }

    @Test
    @DisplayName("로그인 요청 실패 - 카카오 유저 정보 요청 오류")
    void login_fail_kakao_user_info_error() throws Exception {
        // given
        final OAuthRequestDto dto = new OAuthRequestDto("abc");

        // when
        when(authService.login(any(OAuthRequestDto.class)))
                .thenThrow(new BusinessException(AuthErrorCode.KAKAO_TOKEN_ERROR));

        // then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(AuthErrorCode.KAKAO_TOKEN_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.KAKAO_TOKEN_ERROR.getMessage()));
    }

    @Test
    @DisplayName("로그인 요청 실패 - 회원을 찾을 수 없는 경우")
    void login_success_member_not_found() throws Exception {
        final OAuthRequestDto dto = new OAuthRequestDto("abc");

        when(authService.login(any(OAuthRequestDto.class)))
                .thenThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND_ERROR));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_token_success() throws Exception {
        // given
        final ReissueRequestDto requestDto = new ReissueRequestDto("refresh-token");
        final TokenResponseDto tokenResponseDto = new TokenResponseDto("new-access-token", "new-refresh-token", false);
        final Response<TokenResponseDto> response =
                new Response<>(SuccessCode.OK.getCode(), tokenResponseDto, SuccessCode.OK.getMessage());

        // when
        when(authService.reissue(any(ReissueRequestDto.class))).thenReturn(response);

        // then
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()))
                .andExpect(jsonPath("$.content.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.content.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.content.isNew").value(false));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 리프레시 토큰이 유효하지 않은 경우")
    void reissue_token_fail_invalid_refresh_token() throws Exception {
        // given
        final ReissueRequestDto requestDto = new ReissueRequestDto("refresh-token");

        // when
        when(authService.reissue(any(ReissueRequestDto.class))).thenThrow(new BusinessException(AuthErrorCode.REFRESH_TOKEN_ERROR));

        // then
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(AuthErrorCode.REFRESH_TOKEN_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.REFRESH_TOKEN_ERROR.getMessage()));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰이 일치 하지 않은 경우")
    void reissue_token_fail_mismatch_refreshToken() throws Exception {
        // given
        final ReissueRequestDto requestDto = new ReissueRequestDto("refresh-token");

        // when
        when(authService.reissue(any(ReissueRequestDto.class))).thenThrow(new BusinessException(AuthErrorCode.TOKEN_MISMATCH_ERROR));

        // then
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(AuthErrorCode.TOKEN_MISMATCH_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.TOKEN_MISMATCH_ERROR.getMessage()));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        // given
        final User mockUser = mock(User.class);
        final Response<Void> response = new Response<>(SuccessCode.OK.getCode(), null, SuccessCode.OK.getMessage());
        setSecurityContextWithMockUser(mockUser);

        // when
        when(authService.logout(any(User.class), any(HttpServletRequest.class)))
                .thenReturn(response);

        // then
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()));
    }

    @Test
    @DisplayName("로그아웃 실패 - 토큰 파싱시 오류가 발생하는 경우")
    void logout_fail_token_parsing() throws Exception {
        // given
        User mockUser = mock(User.class);
        setSecurityContextWithMockUser(mockUser);

        // when
        when(authService.logout(eq(mockUser), any(HttpServletRequest.class)))
                .thenThrow(new BusinessException(AuthErrorCode.ACCESS_TOKEN_ERROR));

        // then
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(AuthErrorCode.ACCESS_TOKEN_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.ACCESS_TOKEN_ERROR.getMessage()));
    }

    @Test
    @DisplayName("로그아웃 실패 - 존재하지 않는 유저인 경우")
    void logout_fail_not_found_user() throws Exception {
        // given
        User mockUser = mock(User.class);
        setSecurityContextWithMockUser(mockUser);

        // when
        when(authService.logout(eq(mockUser), any(HttpServletRequest.class)))
                .thenThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND_ERROR));

        // then
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
    }

    private void setSecurityContextWithMockUser(User user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, "", List.of()));
        SecurityContextHolder.setContext(context);
    }
}