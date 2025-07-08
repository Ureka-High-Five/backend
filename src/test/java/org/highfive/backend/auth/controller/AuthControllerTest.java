package org.highfive.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.service.AuthService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 요청 성공")
    void login_success() throws Exception {
        // given
        OAuthRequestDto requestDto = new OAuthRequestDto("kakao-code");
        Response mockResponse = new Response<>(200, "access-token", "로그인 성공");

        when(authService.login(any(OAuthRequestDto.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.content", is("access-token")))
                .andExpect(jsonPath("$.message", is("로그인 성공")));
    }

    @Test
    @DisplayName("로그인 요청 실패 - 카카오 토큰 요청 실패")
    void login_token_fail() throws Exception {
        // given
        OAuthRequestDto requestDto = new OAuthRequestDto("invalid-code");

        when(authService.login(any(OAuthRequestDto.class)))
                .thenThrow(new BusinessException(AuthErrorCode.KAKAO_TOKEN_ERROR));

        // when & then
        mockMvc.perform(post("/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is(AuthErrorCode.KAKAO_TOKEN_ERROR.getCode())))
                .andExpect(jsonPath("$.message", is(AuthErrorCode.KAKAO_TOKEN_ERROR.getMessage())));
    }

    @Test
    @DisplayName("로그인 요청 실패 - 사용자 정보 요청 실패")
    void login_userInfo_fail() throws Exception {
        // given
        OAuthRequestDto requestDto = new OAuthRequestDto("invalid-code");

        when(authService.login(any(OAuthRequestDto.class)))
                .thenThrow(new BusinessException(AuthErrorCode.KAKAO_USERINFO_ERROR));

        // when & then
        mockMvc.perform(post("/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code", is(AuthErrorCode.KAKAO_USERINFO_ERROR.getCode())))
                .andExpect(jsonPath("$.message", is(AuthErrorCode.KAKAO_USERINFO_ERROR.getMessage())));
    }
}