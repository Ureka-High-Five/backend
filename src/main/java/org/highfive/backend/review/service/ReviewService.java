package org.highfive.backend.review.service;

import static org.highfive.backend.content.exception.ContentErrorCode.CONTENT_NOT_FOUND;
import static org.highfive.backend.global.code.SuccessCode.CREATED;
import static org.highfive.backend.global.code.SuccessCode.OK;
import static org.highfive.backend.review.exception.ReviewErrorCode.REVIEW_ALREADY_EXISTS;
import static org.highfive.backend.user.exception.UserErrorCode.USER_NOT_FOUND_ERROR;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.review.dto.mapper.ReviewMapper;
import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
import org.highfive.backend.review.dto.request.UpdateReviewRequestDto;
import org.highfive.backend.review.dto.response.ContentMyReviewResponseDto;
import org.highfive.backend.review.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.review.entity.Review;
import org.highfive.backend.review.exception.ReviewErrorCode;
import org.highfive.backend.review.repository.jpa.ReviewRepository;
import org.highfive.backend.slang.SlangValidator;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Response<Void> createReview(final CreateReviewRequestDto requestDto, User user) {
        SlangValidator.validate(requestDto.review());

        Content content = contentRepository.findById(requestDto.contentId())
                .orElseThrow(() -> new BusinessException(CONTENT_NOT_FOUND));

        boolean alreadyCreateReview = reviewRepository.existsByUserIdAndContentId(user.getId(), requestDto.contentId());

        if (alreadyCreateReview) {
            throw new BusinessException(REVIEW_ALREADY_EXISTS);
        }

        Review review = ReviewMapper.toReview(requestDto, user, content);
        reviewRepository.save(review);

        updateUserAverageRating(user.getId());

        return new Response<>(CREATED.getCode(), null, OK.getMessage());

    }

    @Transactional
    public Response<Void> updateReview(final Long reviewId, final UpdateReviewRequestDto requestDto, final User user) {
        SlangValidator.validate(requestDto.review());

        Review existedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!existedReview.isWrittenBy(user.getId())) {
            throw new BusinessException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }

        existedReview.updateReview(requestDto.rating(), requestDto.review());

        updateUserAverageRating(user.getId());

        return new Response<>(OK.getCode(), null, OK.getMessage());

    }

    @Transactional
    public Response<Void> deleteReview(final Long reviewId, final User user) {

        Review existedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!existedReview.isWrittenBy(user.getId())) {
            throw new BusinessException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }

        reviewRepository.delete(existedReview);

        updateUserAverageRating(user.getId());

        return new Response<>(OK.getCode(), null, OK.getMessage());
    }


    public Response<CursorPageResponse<ReviewSimpleResponseDto>> getReviewsByCursor(
            final Long contentId,
            final String cursor,
            final int size,
            final User user) {

        CursorPageResponse<ReviewSimpleResponseDto> items = reviewRepository.findReviewsByCursor(contentId, cursor,
                size, user);
        return Response.ok(items);
    }

    public Response<ContentMyReviewResponseDto> getMyReviewByContent(final Long contentId, final User user) {
        final Optional<Review> opReview = reviewRepository.findByUserIdAndContentId(user.getId(), contentId);

        if (opReview.isPresent()) {
            Review review = opReview.get();
            ContentMyReviewResponseDto dto = new ContentMyReviewResponseDto(review.getRating(), review.getReviewText());
            return new Response<>(OK.getCode(), dto, OK.getMessage());
        }
        return new Response<>(OK.getCode(), null, null);
    }

    public float calculateUserAverageRating(Long userId) {
        final List<Review> reviews = reviewRepository.findAllByUserId(userId);

        if (reviews.isEmpty()) {
            return 0f;
        }

        final double sum = reviews.stream().mapToDouble(Review::getRating).sum();

        return (float) (sum / reviews.size());
    }

    @Transactional
    public void updateUserAverageRating(Long userId) {
        float averageRating = calculateUserAverageRating(userId);

        User exsitedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_ERROR));

        exsitedUser.updateUserAverageRating(averageRating);
    }
}
