package org.highfive.backend.content.controller;

import static org.highfive.backend.global.code.SuccessCode.NO_CONTENT;
import static org.highfive.backend.global.code.SuccessCode.OK;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.request.CreateReviewRequestDto;
import org.highfive.backend.content.dto.request.UpdateReviewRequestDto;
import org.highfive.backend.content.dto.response.ReviewSimpleResponseDto;
import org.highfive.backend.content.service.ReviewService;
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
    public Response<?> createReview(@Valid @RequestBody CreateReviewRequestDto requestDto,
                                    @AuthenticationPrincipal User user) {
        return reviewService.createReview(requestDto, user);
    }

    @PatchMapping("/{reviewId}")
    public Response<?> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        return reviewService.updateReview(reviewId, requestDto, user);
    }

    @DeleteMapping("/{reviewId}")
    public Response<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user) {

        return reviewService.deleteReview(reviewId, user);
    }

    @GetMapping("/{contentId}")
    public Response<CursorPageResponse<ReviewSimpleResponseDto>> getReviewByContent(
            @PathVariable Long contentId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "3") int size
    ) {
        CursorPageResponse<ReviewSimpleResponseDto> response = reviewService.getReviewsByCursor(contentId, cursor,
                size);

        if (response.items() == null || response.items().isEmpty()) {
            return new Response<>(NO_CONTENT.getCode(), null, null);
        }

        return new Response<>(OK.getCode(), response, null);
    }

}
