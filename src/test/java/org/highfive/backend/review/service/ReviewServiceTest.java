//package org.highfive.backend.review.service;
//
//import static org.highfive.backend.content.exception.ContentErrorCode.CONTENT_NOT_FOUND;
//import static org.highfive.backend.review.exception.ReviewErrorCode.MY_REVIEW_NOT_FOUND;
//import static org.highfive.backend.review.exception.ReviewErrorCode.REVIEW_FORBIDDEN;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//import org.highfive.backend.common.fixture.ContentFixture;
//import org.highfive.backend.common.fixture.ReviewFixture;
//import org.highfive.backend.common.fixture.UserFixture;
//import org.highfive.backend.content.entity.Content;
//import org.highfive.backend.content.repository.jpa.ContentRepository;
//import org.highfive.backend.global.dto.Response;
//import org.highfive.backend.global.exception.BusinessException;
//import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
//import org.highfive.backend.review.dto.request.UpdateReviewRequestDto;
//import org.highfive.backend.review.dto.response.ContentMyReviewResponseDto;
//import org.highfive.backend.review.entity.Review;
//import org.highfive.backend.review.repository.jpa.ReviewRepository;
//import org.highfive.backend.user.entity.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//public class ReviewServiceTest {
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private ContentRepository contentRepository;
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    private User user, anotherUser;
//    private Content content;
//    private Review review;
//
//    @BeforeEach
//    void setUp() {
//        user = UserFixture.createUser(1L);
//        anotherUser = UserFixture.createUser(999L);
//        content = ContentFixture.createContent(2L);
//        review = ReviewFixture.createReview(3L, user, content);
//    }
//
//    @Test
//    @DisplayName("리뷰 생성에 성공하면 201 코드를 반환한다")
//    void createReview_success() {
//        //given
//        CreateReviewRequestDto reviewRequestDto = new CreateReviewRequestDto(2L, 5, "재밌어요!");
//        when(contentRepository.findById(reviewRequestDto.contentId())).thenReturn(Optional.of(content));
//        when(reviewRepository.save(any())).thenReturn(review);
//
//        //when
//        Response<?> response = reviewService.createReview(reviewRequestDto, user);
//
//        //then
//        assertEquals(20100, response.code());
//        verify(reviewRepository).save(any());
//
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 컨텐츠에 대하여 리뷰를 생성하려고하면 예외를 던진다")
//    void createReview_fail_contentNotFound() {
//        // given
//        CreateReviewRequestDto dto = new CreateReviewRequestDto(999L, 1, "존재하지않는 컨텐츠에 대한 리뷰");
//        when(contentRepository.findById(dto.contentId())).thenReturn(Optional.empty());
//
//        // when
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            reviewService.createReview(dto, user);
//        });
//
//        // then
//        assertEquals(CONTENT_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("리뷰 수정에 성공하면 200 성공 코드를 반환한다")
//    void updateReview_success() {
//        //given
//        UpdateReviewRequestDto dto = new UpdateReviewRequestDto("리뷰 수정합니다", 3);
//        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
//
//        // when
//        Response<?> response = reviewService.updateReview(3L, dto, user);
//
//        //then
//        assertEquals(20000, response.code());
//        assertEquals("리뷰 수정합니다", review.getReviewText());
//        assertEquals(3, review.getRating());
//    }
//
//    @Test
//    @DisplayName("다른 유저의 리뷰를 수정 하려고하면 예외가 발생한다")
//    void updateReview_fail_forbiddenUser() {
//        // given
//        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
//
//        // when
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            reviewService.updateReview(3L, new UpdateReviewRequestDto("남의 리뷰 수정 시도", 1), anotherUser);
//        });
//
//        //then
//        assertEquals(REVIEW_FORBIDDEN, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("리뷰 삭제에 성공하면 200 코드를 반환한다")
//    void deleteReview_success() {
//        // given
//        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
//
//        // when
//        Response<?> response = reviewService.deleteReview(3L, user);
//
//        // then
//        assertEquals(20000, response.code());
//        verify(reviewRepository).delete(review);
//    }
//
//    @Test
//    @DisplayName("다른 유저가 리뷰를 삭제하려고 하면 예외가 발생한다")
//    void deleteReview_fail_forbiddenUser() {
//        // given
//        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
//
//        // when
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            reviewService.deleteReview(3L, anotherUser);
//        });
//
//        // then
//        assertEquals(REVIEW_FORBIDDEN, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("내가 작성한 콘텐츠 리뷰가 있다면 해당 내용을 반환한다")
//    void getMyReviewByContent_success() {
//        // given
//        when(reviewRepository.findByUserIdAndContentId(user.getId(), content.getId())).thenReturn(Optional.of(review));
//
//        // when
//        Response<ContentMyReviewResponseDto> response = reviewService.getMyReviewByContent(content.getId(), user);
//
//        // then
//        assertEquals(20000, response.code());
//        assertEquals(review.getReviewText(), response.content().review());
//        assertEquals(review.getRating(), response.content().rating());
//    }
//
//    @Test
//    @DisplayName("내가 작성한 리뷰가 없다면 예외가 발생한다")
//    void getMyReviewByContent_fail_reviewNotFound() {
//        // given
//        when(reviewRepository.findByUserIdAndContentId(user.getId(), content.getId())).thenReturn(Optional.empty());
//
//        // when
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            reviewService.getMyReviewByContent(content.getId(), user);
//        });
//
//        // then
//        assertEquals(MY_REVIEW_NOT_FOUND, exception.getErrorCode());
//    }
//
//}
