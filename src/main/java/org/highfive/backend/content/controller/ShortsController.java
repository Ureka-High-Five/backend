package org.highfive.backend.content.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.request.ShortsLikeRequestDto;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.service.ShortsService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorts")
@RequiredArgsConstructor
public class ShortsController {

    private final ShortsService shortsService;

    @GetMapping
    public Response<RecommendShortsResponseDto> recommendShorts(
            @RequestParam(required = false) @NotEmpty final Long cursor,
            @RequestParam(defaultValue = "5") @Positive final Integer size
    ) {
        return shortsService.recommendShorts(cursor, size);
    }

    @PostMapping("/like")
    public Response<Void> like(@AuthenticationPrincipal final User user, @RequestBody @Valid final ShortsLikeRequestDto dto) {
        return shortsService.like(user, dto);
    }
}
