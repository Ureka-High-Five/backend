package org.highfive.backend.content.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.shorts.controller.ShortsController;
import org.highfive.backend.shorts.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.shorts.service.ShortsService;
import org.highfive.backend.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(ShortsController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShortsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ShortsService shortsService;

    User userFixture = UserFixture.createEmbeddingUser();

    private static Response<RecommendShortsResponseDto> dummyResponse() {
        return Response.ok(new RecommendShortsResponseDto(null, "SHORTS"));
    }

    private static RequestPostProcessor withUser(User domainUser) {
        return request -> {
            var auth = new UsernamePasswordAuthenticationToken(
                    domainUser,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(auth);
            return request;
        };
    }

    @Test
    @DisplayName("cursor가 null인 경우 정상적으로 응답합니다.")
    void cursorNull_test() throws Exception {
        given(shortsService.recommendShorts(isNull(), eq(5), any(User.class))).willReturn(dummyResponse());

        mockMvc.perform(get("/shorts?size=5").with(withUser(userFixture)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("20000"));
    }

    @Test
    @DisplayName("cursor가 타입 캐스팅이 정상적으로 이루어집니다.")
    void cursorTypeCasting_test() throws Exception {
        given(shortsService.recommendShorts(eq(5L), eq(5), any(User.class))).willReturn(dummyResponse());

        mockMvc.perform(get("/shorts?cursor=5&size=5").with(withUser(userFixture)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("20000"));
    }

    @Test
    @DisplayName("size가 null인 경우 기본값으로 정상적으로 이루어집니다.")
    void defaultSize_test() throws Exception {
        given(shortsService.recommendShorts(eq(5L), eq(5), any(User.class))).willReturn(dummyResponse());

        mockMvc.perform(get("/shorts?cursor=5").with(withUser(userFixture)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("20000"));
    }
}