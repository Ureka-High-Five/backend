package org.highfive.backend.review.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.highfive.backend.review.entity.Review;

public record ReviewSimpleResponseDto(
        Long reviewId,
        String userProfileUrl,
        int userRating,
        String userReview

) {

    @QueryProjection
    public ReviewSimpleResponseDto(Review review) {
        this(
                review.getId(),
                review.getUser().getProfileUrl(),
                review.getRating(),
                review.getReviewText()
        );
    }
}
