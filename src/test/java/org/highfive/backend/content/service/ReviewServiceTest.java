package org.highfive.backend.content.service;

import static com.mysema.commons.lang.Assert.assertThat;
import static org.highfive.backend.content.exception.ContentErrorCode.CONTENT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.highfive.backend.common.fixture.ContentFixture;
import org.highfive.backend.common.fixture.ReviewFixture;
import org.highfive.backend.common.fixture.UserFixture;
import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.dto.request.UpdateReviewRequestDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.review.Review;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.ReviewRepository;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Content content;
    private Review review;

    @BeforeEach
    void setUp() {
        user = UserFixture.createUser(1L);
        content = ContentFixture.createContent(2L);
        review = ReviewFixture.createReview(3L, user, content);
    }

    @Test
    @DisplayName("리뷰 생성에 성공하면 201 코드를 반환한다")
    void createReview_success(){
        //given
        CreateReviewRequestDto reviewRequestDto = new CreateReviewRequestDto(2L, 5, "재밌어요!");
        when(contentRepository.findById(reviewRequestDto.contentId())).thenReturn(Optional.of(content));
        when(reviewRepository.save(any())).thenReturn(review);

        //when
        Response<?> response = reviewService.createReview(reviewRequestDto, user);

        //then
        assertEquals(20100, response.code());
        verify(reviewRepository).save(any());

    }

    @Test
    @DisplayName("존재하지 않는 컨텐츠에 대하여 리뷰를 생성하려고하면 예외를 던진다")
    void createReview_fail_contentNotFound() {
        // given
        CreateReviewRequestDto dto = new CreateReviewRequestDto(999L, 1, "존재하지않는 컨텐츠에 대한 리뷰");
        when(contentRepository.findById(dto.contentId())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reviewService.createReview(dto, user);
        });

        // then
        assertEquals(CONTENT_NOT_FOUND, exception.getErrorCode());
    }






}
