package org.highfive.backend.content.controller;

import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.service.ReviewService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content/review")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public Response<?> createReview(@RequestBody CreateReviewRequestDto request, @AuthenticationPrincipal User user){
		return reviewService.createReview(request, user);
	}
}
