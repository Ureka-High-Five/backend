package org.highfive.backend.content.integration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.ShortsFixture;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.dto.response.ShortsItemDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.jpa.ShortsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ShortsRecommendIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ShortsRepository shortsRepository;

    @BeforeEach
    void setUp() {
        Content c1 = contentRepository.save(ContentFixture.createDefaultContent());
        Content c2 = contentRepository.save(ContentFixture.createDefaultContent());

        for (int i = 0; i < 4; i++) shortsRepository.save(ShortsFixture.createShorts(c1));
        for (int i = 0; i < 4; i++) shortsRepository.save(ShortsFixture.createShorts(c2));
    }

    @Test
    @DisplayName("추천 쇼츠 조회 통합 테스트 - cursor=null, size=5인 경우")
    void cursorNullAndSize_ok() throws Exception {
        mockMvc.perform(get("/shorts").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.shorts.items", hasSize(5)))
                .andExpect(jsonPath("$.content.shorts.hasNext").value(true));
    }

    @Test
    @DisplayName("추천 쇼츠 조회 통합 테스트 - cursor=1, size=null인 경우")
    void cursorAndSizeNull_ok() throws Exception {
        mockMvc.perform(get("/shorts").param("cursor","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.shorts.items", hasSize(5)))
                .andExpect(jsonPath("$.content.shorts.hasNext").value(true));
    }
}
