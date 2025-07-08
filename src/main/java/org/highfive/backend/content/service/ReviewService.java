package org.highfive.backend.content.service;

import static org.highfive.backend.global.code.SuccessCode.CREATED;
import static org.highfive.backend.global.code.SuccessCode.OK;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.mapper.ReviewMapper;
import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.dto.request.UpdateReviewRequestDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.review.Review;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.exception.ReviewErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.ReviewRepository;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentRepository contentRepository;

    public Response<?> createReview(CreateReviewRequestDto requestDto, User user) {

        // TODO: review에 대한 금칙어 처리 추가 필요

        Content content = contentRepository.findById(requestDto.contentId())
                .orElseThrow(() -> new BusinessException(ContentErrorCode.CONTENT_001));

        Review review = ReviewMapper.toReview(requestDto, user, content);
        reviewRepository.save(review);

        return new Response<>(CREATED.getCode(), null, null);

    }

    public Response<?> updateReview(Long reviewId, UpdateReviewRequestDto requestDto, User user) {
        // TODO: review에 대한 금칙어 처리 추가 필요

        Review existedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_002));

        existedReview.updateReview(requestDto.rating, requestDto.review);

        return new Response<>(OK.getCode(), null, null);

    }
}
