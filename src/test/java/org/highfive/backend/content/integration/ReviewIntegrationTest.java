//package org.highfive.backend.content.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.transaction.Transactional;
//import java.util.List;
//import org.highfive.backend.common.fixture.ContentFixture;
//import org.highfive.backend.common.fixture.ReviewFixture;
//import org.highfive.backend.common.fixture.UserFixture;
//import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
//import org.highfive.backend.review.dto.request.UpdateReviewRequestDto;
//import org.highfive.backend.content.entity.Content;
//import org.highfive.backend.review.entity.Review;
//import org.highfive.backend.content.repository.jpa.ContentRepository;
//import org.highfive.backend.review.repository.jpa.ReviewRepository;
//import org.highfive.backend.user.entity.User;
//import org.highfive.backend.user.repository.jpa.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.RequestPostProcessor;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.highfive.backend.content.exception.ContentErrorCode.CONTENT_NOT_FOUND;
//import static org.highfive.backend.review.exception.ReviewErrorCode.MY_REVIEW_NOT_FOUND;
//import static org.highfive.backend.review.exception.ReviewErrorCode.REVIEW_NOT_FOUND;
//import static org.highfive.backend.global.code.SuccessCode.NO_CONTENT;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)   // → Security 필터 전부 비활성화
//@Transactional
//class ReviewIntegrationTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    ContentRepository contentRepository;
//
//    @Autowired
//    ReviewRepository reviewRepository;
//
//    User user;
//    Content content;
//    Long reviewId;
//
//    private static RequestPostProcessor withUser(User principal) {
//        return req -> {
//            Authentication auth = new UsernamePasswordAuthenticationToken(
//                    principal, null,
//                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
//            );
//            SecurityContextHolder.clearContext();
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            req.setUserPrincipal(auth);
//            return req;
//        };
//    }
//
//    @BeforeEach
//    void init() {
//        user    = userRepository.save(UserFixture.createUser(null));
//        content = contentRepository.save(ContentFixture.createContent(null));
//
//        Review review = reviewRepository.save(ReviewFixture.createReview(null, user, content));
//        reviewId = review.getId();
//    }
//
//    @Nested class CreateReview {
//        @Test
//        @DisplayName("리뷰 생성에 성공한다")
//        void createReview_success() throws Exception {
//            CreateReviewRequestDto dto = new CreateReviewRequestDto(content.getId(), 4, "굿!");
//
//            mockMvc.perform(post("/content/review")
//                            .with(withUser(user))
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.code").value(20100));
//
//            assertThat(reviewRepository.count()).isEqualTo(2); // 기본 1건 + 새 리뷰
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 콘텐츠에 리뷰를 생성하려고 하면 예외가 발생한다")
//        void createReview_contentNotFound() throws Exception {
//            CreateReviewRequestDto dto = new CreateReviewRequestDto(999_999L, 3, "x");
//
//            mockMvc.perform(post("/content/review")
//                            .with(withUser(user))
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.code").value(CONTENT_NOT_FOUND.getCode()));
//        }
//    }
//
//    @Nested class UpdateReview {
//
//        @Test
//        @DisplayName("리뷰 수정에 성공한다")
//        void update_success() throws Exception {
//            UpdateReviewRequestDto dto = new UpdateReviewRequestDto("수정한 리뷰", 3);
//
//            mockMvc.perform(patch("/content/review/{id}", reviewId)
//                            .with(withUser(user))
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.code").value(20000));
//
//            Review updated = reviewRepository.findById(reviewId).orElseThrow();
//            assertThat(updated.getRating()).isEqualTo(3);
//            assertThat(updated.getReviewText()).isEqualTo("수정한 리뷰");
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 리뷰를 수정하려고 하면 예외가 발생한다")
//        void update_notFound() throws Exception {
//            UpdateReviewRequestDto dto = new UpdateReviewRequestDto("x", 1);
//
//            mockMvc.perform(patch("/content/review/{id}", 999_999L)
//                            .with(withUser(user))
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.code").value(REVIEW_NOT_FOUND.getCode()));
//        }
//    }
//
//
//    @Nested class DeleteReview {
//
//        @Test
//        @DisplayName("리뷰를 성공적으로 삭제한다")
//        void delete_success() throws Exception {
//            mockMvc.perform(delete("/content/review/{id}", reviewId)
//                            .with(withUser(user)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.code").value(20000));
//
//            assertThat(reviewRepository.existsById(reviewId)).isFalse();
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 리뷰를 삭제하려고하면 예외가 발생한다")
//        void delete_notFound() throws Exception {
//            mockMvc.perform(delete("/content/review/{id}", 999_999L)
//                            .with(withUser(user)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.code").value(REVIEW_NOT_FOUND.getCode()));
//        }
//    }
//
//    @Nested class ListReviewsByContent {
//
//        @Test
//        @DisplayName("리뷰 목록 조회 성공")
//        void list_success() throws Exception {
//            mockMvc.perform(get("/content/review/{contentId}", content.getId())
//                            .queryParam("size", "3"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.code").value(20000))
//                    .andExpect(jsonPath("$.content.items[0].reviewId").value(reviewId));
//        }
//
//        @Test
//        @DisplayName("리뷰가 존재하지 않으면 hasNext가 false가 된다.")
//        void list_noContent() throws Exception {
//            Long emptyContentId = contentRepository.save(ContentFixture.createContent(null)).getId();
//
//            mockMvc.perform(get("/content/review/{contentId}", emptyContentId))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.content.hasNext").value(false));
//        }
//    }
//
//
//    @Nested class MyReview {
//
//        @Test
//        @DisplayName("내 리뷰 조회에 성공한다")
//        void myReview_success() throws Exception {
//            mockMvc.perform(get("/content/review/{contentId}/me", content.getId())
//                            .with(withUser(user)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.content.review").value("좋아요"));
//        }
//
//        @Test
//        @DisplayName("남긴 리뷰가 없으면 예외가 발생한다")
//        void myReview_notFound() throws Exception {
//            Long otherContentId = contentRepository.save(ContentFixture.createContent(null)).getId();
//
//            mockMvc.perform(get("/content/review/{contentId}/me", otherContentId)
//                            .with(withUser(user)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.code").value(MY_REVIEW_NOT_FOUND.getCode()));
//        }
//    }
//}
