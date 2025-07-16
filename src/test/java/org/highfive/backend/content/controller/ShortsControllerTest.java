package org.highfive.backend.content.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.highfive.backend.shorts.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.shorts.service.ShortsService;
import org.highfive.backend.global.dto.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShortsController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShortsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ShortsService shortsService;

    private static Response<RecommendShortsResponseDto> dummyResponse() {
        return Response.ok(new RecommendShortsResponseDto(null, "SHORTS"));
    }

    @Test
    @DisplayName("cursor가 null인 경우 정상적으로 응답합니다.")
    void cursorNull_test() throws Exception {
        given(shortsService.recommendShorts(null, 5)).willReturn(dummyResponse());

        mockMvc.perform(get("/shorts?size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("20000"));
    }

    @Test
    @DisplayName("cursor가 타입 캐스팅이 정상적으로 이루어집니다.")
    void cursorTypeCasting_test() throws Exception {
        given(shortsService.recommendShorts(5L, 5)).willReturn(dummyResponse());

        mockMvc.perform(get("/shorts?cursor=5&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("20000"));
    }

    @Test
    @DisplayName("size가 null인 경우 기본값으로 정상적으로 이루어집니다.")
    void defaultSize_test() throws Exception {
        given(shortsService.recommendShorts(5L, 5)).willReturn(dummyResponse());

        mockMvc.perform(get("/shorts?cursor=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("20000"));
    }
}