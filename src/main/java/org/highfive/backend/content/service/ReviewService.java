package org.highfive.backend.content.service;

import static org.highfive.backend.content.exception.ContentErrorCode.*;
import static org.highfive.backend.global.code.SuccessCode.CREATED;
import static org.highfive.backend.global.code.SuccessCode.OK;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ReviewMapper;
import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.dto.request.UpdateReviewRequestDto;
import org.highfive.backend.content.dto.response.ContentMyReviewResponseDto;
import org.highfive.backend.content.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.review.Review;
import org.highfive.backend.content.exception.ReviewErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.ReviewRepository;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentRepository contentRepository;

    public Response<?> createReview(final CreateReviewRequestDto requestDto, User user) {

        // TODO: review에 대한 금칙어 처리 추가 필요

        Content content = contentRepository.findById(requestDto.contentId())
                .orElseThrow(() -> new BusinessException(CONTENT_NOT_FOUND));

        Review review = ReviewMapper.toReview(requestDto, user, content);
        reviewRepository.save(review);

        return new Response<>(CREATED.getCode(), null, OK.getMessage());

    }

    public Response<?> updateReview(final Long reviewId, final UpdateReviewRequestDto requestDto, final User user) {
        // TODO: review에 대한 금칙어 처리 추가 필요

        Review existedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!existedReview.isWrittenBy(user.getId())) {
            throw new BusinessException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }

        existedReview.updateReview(requestDto.rating(), requestDto.review());
        return new Response<>(OK.getCode(), null, OK.getMessage());

    }

    public Response<?> deleteReview(final Long reviewId, final User user) {

        Review existedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!existedReview.isWrittenBy(user.getId())) {
            throw new BusinessException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }

        reviewRepository.delete(existedReview);

        return new Response<>(OK.getCode(), null, OK.getMessage());
    }


    public CursorPageResponse<ReviewSimpleResponseDto> getReviewsByCursor(final Long contentId, final String cursor,
                                                                          final int size) {

        return reviewRepository.findReviewsByCursor(contentId, cursor,
                size);
    }

    public Response<ContentMyReviewResponseDto> getMyReviewByContent(final Long contentId, final User user) {
        final Review review = reviewRepository.findByUserIdAndContentId(user.getId(), contentId).orElseThrow(() -> new BusinessException(CONTENT_NOT_FOUND));
        final ContentMyReviewResponseDto contentMyReviewResponseDto = new ContentMyReviewResponseDto(review.getRating(), review.getReviewText());
        return new Response<>(OK.getCode(), contentMyReviewResponseDto, OK.getMessage());
    }
}
