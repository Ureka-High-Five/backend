package org.highfive.backend.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.review.dto.request.CreateReviewRequestDto;
import org.highfive.backend.review.dto.request.UpdateReviewRequestDto;
import org.highfive.backend.review.dto.response.ContentMyReviewResponseDto;
import org.highfive.backend.review.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.review.service.ReviewService;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Response<Void> createReview(@Valid @RequestBody final CreateReviewRequestDto requestDto,
                                    @AuthenticationPrincipal final User user) {
        return reviewService.createReview(requestDto, user);
    }

    @PatchMapping("/{reviewId}")
    public Response<Void> updateReview(
            @PathVariable final Long reviewId,
            @Valid @RequestBody final UpdateReviewRequestDto requestDto,
            @AuthenticationPrincipal final User user) {

        return reviewService.updateReview(reviewId, requestDto, user);
    }

    @DeleteMapping("/{reviewId}")
    public Response<Void> deleteReview(
            @PathVariable final Long reviewId,
            @AuthenticationPrincipal final User user) {

        return reviewService.deleteReview(reviewId, user);
    }

    @GetMapping("/{contentId}")
    public Response<CursorPageResponse<ReviewSimpleResponseDto>> getReviewByContent(
            @PathVariable final Long contentId,
            @RequestParam(required = false) final String cursor,
            @RequestParam(defaultValue = "3") final int size,
            @AuthenticationPrincipal final User user
    ) {
        return reviewService.getReviewsByCursor(contentId, cursor, size, user);
    }

    @GetMapping("/{contentId}/me")
    public Response<ContentMyReviewResponseDto> getMyReviewByContent(final @PathVariable Long contentId, @AuthenticationPrincipal final User user) {
        return reviewService.getMyReviewByContent(contentId, user);
    }
}
