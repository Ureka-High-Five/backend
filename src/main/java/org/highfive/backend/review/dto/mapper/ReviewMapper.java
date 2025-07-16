package org.highfive.backend.review.dto.mapper;

import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.review.entity.Review;
import org.highfive.backend.user.entity.User;

public class ReviewMapper {
	public static Review toReview(CreateReviewRequestDto requestDto, User user, Content content) {
		return Review.of(
			requestDto.rating(),
			requestDto.review(),
			user,
			content
		);
	}
}
