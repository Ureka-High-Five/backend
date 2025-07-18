package org.highfive.backend.shorts.dto.response;

import org.highfive.backend.global.dto.CursorPageResponse;

public record RecommendShortsResponseDto(
        CursorPageResponse<ShortsResponseDto> shorts
) {
}
