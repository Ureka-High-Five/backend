package org.highfive.backend.content.dto.response;

import org.highfive.backend.global.dto.CursorPageResponse;

public record RecommendShortsResponseDto(
        CursorPageResponse<ShortsItemDto> shorts,
        String videoType
) {
}
