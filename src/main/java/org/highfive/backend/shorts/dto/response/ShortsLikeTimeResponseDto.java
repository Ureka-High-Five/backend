package org.highfive.backend.shorts.dto.response;

import java.util.List;

public record ShortsLikeTimeResponseDto(
        List<ShortsLikeTimeLineDto> likeTimeLines
) {
    public record ShortsLikeTimeLineDto(
            int time,
            int count
    ) {}
}
