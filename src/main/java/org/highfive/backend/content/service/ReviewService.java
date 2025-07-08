package org.highfive.backend.content.service;

import org.highfive.backend.content.dto.mapper.ReviewMapper;
import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.entity.Content;
import org.highfive.backend.content.entity.review.Review;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.ContentRepository;
import org.highfive.backend.content.repository.ReviewRepository;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;

import static org.highfive.backend.global.code.SuccessCode.REVIEW_CREATED;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ContentRepository contentRepository;

	public Response<?> createReview(CreateReviewRequestDto requestDto, User user) {

		Content content = contentRepository.findById(requestDto.contentId())
			.orElseThrow(()-> new BusinessException(ContentErrorCode.CONTENT_001));

		Review review = ReviewMapper.toReview(requestDto, user, content);
		reviewRepository.save(review);

		return new Response<>(REVIEW_CREATED.getCode(),null,null);

	}
}
