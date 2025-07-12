package org.highfive.backend.content.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.ReviewRepository;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.highfive.backend.content.exception.ContentErrorCode.CONTENT_NOT_FOUND;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ReviewIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ContentRepository contentRepository;
    @Autowired private ReviewRepository reviewRepository;

    private User user;
    private Long contentId;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser(null));
        contentId = contentRepository.save(ContentFixture.createContent(null)).getId();

        auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("리뷰 생성에 성공한다")
    void createReview_success() throws Exception {
        // given
        CreateReviewRequestDto dto = new CreateReviewRequestDto(contentId, 5, "재미있어요!");

        // when & then
        mockMvc.perform(post("/content/review")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20100));

        assertThat(reviewRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠에 리뷰를 생성하려고 하면 예외가 발생한다")
    void createReview_contentNotFound() throws Exception {
        // given
        Long wrongContentId = 999_999L;
        CreateReviewRequestDto dto = new CreateReviewRequestDto(wrongContentId, 4, "리뷰");

        // when & then
        mockMvc.perform(post("/content/review")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(CONTENT_NOT_FOUND.getCode()));
    }
}
