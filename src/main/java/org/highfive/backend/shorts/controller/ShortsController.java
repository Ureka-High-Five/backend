package org.highfive.backend.shorts.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.shorts.dto.request.ShortsDislikeRequestDto;
import org.highfive.backend.shorts.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.shorts.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.shorts.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.shorts.service.ShortsService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shorts")
public class ShortsController {

    private final ShortsService shortsService;

    @PostMapping("/comment")
    public Response<Void> createShortsComment(
            @Valid @RequestBody final CreateShortsCommentRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return shortsService.createShortsComment(requestDto, user);
    }

    @GetMapping
    public Response<RecommendShortsResponseDto> recommendShorts(
            @RequestParam(required = false) @Positive final Long cursor,
            @RequestParam(defaultValue = "5", required = false) @Positive final Integer size,
            @AuthenticationPrincipal User user
    ) {
        return shortsService.recommendShorts(cursor, size, user);
    }

    @PostMapping("/like")
    public Response<Void> like(@AuthenticationPrincipal final User user, @RequestBody @Valid final ShortsLikeRequestDto dto) {
        return shortsService.like(user, dto);
    }

    @PostMapping("/dislike")
    public Response<Void> dislike(@AuthenticationPrincipal final User user, @RequestBody @Valid final ShortsDislikeRequestDto dto) {
        return shortsService.dislike(user, dto);
    }
}
