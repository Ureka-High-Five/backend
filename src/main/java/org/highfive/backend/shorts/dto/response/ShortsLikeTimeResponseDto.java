package org.highfive.backend.shorts.dto.response;

import java.util.List;

public record ShortsLikeTimeResponseDto(
        List<ShortsLikeTimeLineDto> likeTimeLines,
        boolean liked
) {
    public record ShortsLikeTimeLineDto(
            int time,
            int count
    ) {
    }
}
