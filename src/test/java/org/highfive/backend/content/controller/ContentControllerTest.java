package org.highfive.backend.content.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Map;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.GenreContentDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.MainRecommendDto;
import org.highfive.backend.content.dto.response.HomeContentsResponseDto.PersonalRecommendDto;
import org.highfive.backend.content.service.ContentService;
import org.highfive.backend.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(ContentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ContentService contentService;

    private static RequestPostProcessor withUser(User principal) {
        return req -> {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal, null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(auth);
            req.setUserPrincipal(auth);
            return req;
        };
    }

    @Test
    @DisplayName("홈 화면 컨텐츠 추천 – 올바른 dto가 생성됩니다")
    void homeContents_success() throws Exception {
        // given
        MainRecommendDto main = new MainRecommendDto("main.jpg", "main title", List.of("Action", "Thriller"));
        List<PersonalRecommendDto> personal = List.of(
                new PersonalRecommendDto(200L, "p1.jpg"),
                new PersonalRecommendDto(201L, "p2.jpg"),
                new PersonalRecommendDto(202L, "p3.jpg"),
                new PersonalRecommendDto(203L, "p4.jpg")
        );
        Map<String, List<GenreContentDto>> genre =
                Map.of("Action", List.of(new GenreContentDto(300L, "g1.jpg")));

        when(contentService.recommendMainContentsByUser(any()))
                .thenReturn(main);
        when(contentService.recommendContentsByUser(any(), eq(4)))
                .thenReturn(personal);
        when(contentService.recommendContentsByUserGenre(any(), eq(2)))
                .thenReturn(genre);

        // when, then
        mockMvc.perform(get("/content/recommend").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20000))
                .andExpect(jsonPath("$.content.mainRecommend.posterUrl").value("main.jpg"))
                .andExpect(jsonPath("$.content.personalRecommends[0].contentId").value(200L))
                .andExpect(jsonPath("$.content.genre.Action[0].contentId").value(300L));
    }
}