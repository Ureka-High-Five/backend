package org.highfive.backend.content.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.content.dto.response.RecommendShortsResponseDto;
import org.highfive.backend.content.dto.response.ShortsItemDto;
import org.highfive.backend.content.service.ShortsService;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortsController {

    private final ShortsService shortsService;

    @GetMapping("/shorts")
    public Response<RecommendShortsResponseDto> recommendShorts(
            @RequestParam(required = false) @NotEmpty final Long cursor,
            @RequestParam(defaultValue = "5") @Positive final Integer size
    ) {
        return shortsService.mockRecommendShorts(cursor, size);
    }
}
