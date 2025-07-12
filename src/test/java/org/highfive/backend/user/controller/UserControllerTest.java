package org.highfive.backend.user.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Test
    @DisplayName("온보딩 제출 - 사용자 정보가 성공적으로 저장되면 올바른 응답을 반환합니다.")
    void submitOnboarding_success() throws Exception {
        // given
        SubmitOnboardingRequestDto req =
                new SubmitOnboardingRequestDto(
                        1L,
                        List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L),
                        1998,
                        Gender.MALE,
                        "홍길동"
                );

        TokenResponseDto tokens = new TokenResponseDto(
                "access-token",
                "refresh-token",
                true
        );
        Response<TokenResponseDto> serviceRes = new Response<>(20000, tokens, "요청이 정상 처리되었습니다.");

        given(userService.initUser(req)).willReturn(serviceRes);

        // when, then
        mockMvc.perform(
                        patch("/user/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20000))
                .andExpect(jsonPath("$.content.accessToken").value("access-token"))
                .andExpect(jsonPath("$.content.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.message").value("요청이 정상 처리되었습니다."));

    }

    @Test
    @DisplayName("온보딩 제출 - 10개 미만의 작품을 선택할 경우 Bad Request가 발생합니다")
    void submitOnboarding_fail_lessThan10Contents() throws Exception {
        // given
        SubmitOnboardingRequestDto req =
                new SubmitOnboardingRequestDto(
                        1L,
                        List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L),
                        1998,
                        Gender.MALE,
                        "홍길동"
                );

        TokenResponseDto tokens = new TokenResponseDto(
                "access-token",
                "refresh-token",
                true
        );
        Response<TokenResponseDto> serviceRes = new Response<>(20000, tokens, "요청이 정상 처리되었습니다.");

        given(userService.initUser(req)).willReturn(serviceRes);

        // when, then
        mockMvc.perform(
                        patch("/user/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}