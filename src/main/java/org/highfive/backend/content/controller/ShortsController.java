package org.highfive.backend.content.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.dto.request.CreateShortsCommentRequestDto;
import org.highfive.backend.content.service.ShortsService;
import org.highfive.backend.global.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "5") @Positive final Integer size
    ) {
        return shortsService.recommendShorts(cursor, size);
    }

    @PostMapping("/like")
    public Response<Void> like(@AuthenticationPrincipal final User user, @RequestBody @Valid final ShortsLikeRequestDto dto) {
        return shortsService.like(user, dto);
    }
}
