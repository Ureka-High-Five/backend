package org.highfive.backend.content.controller;

import static org.mockito.BDDMockito.given;

import java.util.List;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.content.service.OnboardingService;
import org.highfive.backend.global.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(OnboardingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContentService contentService;

    @MockitoBean
    private OnboardingService onboardingService;

    @Test
    @DisplayName("온보딩 초기 화면 - 컨트롤러가 올바른 응답을 보냅니다.")
    void onboardingInitContents_returnsDistinctGenreTop6() throws Exception {
        // given
        List<OnboardingInitContentsResponseDto> dtoList = List.of(
                new OnboardingInitContentsResponseDto(1L, "url1", "title1", 2024),
                new OnboardingInitContentsResponseDto(2L, "url2", "title2", 2024)
        );
        given(contentService.getDistinctGenreTopContents()).willReturn(dtoList);

        // when, then
        mockMvc.perform(get("/content/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20000))
                .andExpect(jsonPath("$.content", hasSize(dtoList.size())))
                .andExpect(jsonPath("$.content[0].contentId").value(1L))
                .andExpect(jsonPath("$.content[0].thumbnailUrl").value("url1"))
                .andExpect(jsonPath("$.content[0].title").value("title1"))
                .andExpect(jsonPath("$.content[0].openYear").value(2024))
                .andExpect(jsonPath("$.content[1].contentId").value(2L))
                .andExpect(jsonPath("$.content[1].thumbnailUrl").value("url2"))
                .andExpect(jsonPath("$.content[1].title").value("title2"))
                .andExpect(jsonPath("$.content[1].openYear").value(2024));;
    }
}