package org.highfive.backend.shorts.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record ShortsCommentsResponseDto(
        Long commentId,
        String userName,
        Long userId,
        String profileUrl,
        String comment,
        String createdAt
) {
    @QueryProjection
    public ShortsCommentsResponseDto {
    }
}
