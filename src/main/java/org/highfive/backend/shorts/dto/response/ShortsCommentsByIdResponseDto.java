package org.highfive.backend.shorts.dto.response;

public record ShortsCommentsByIdResponseDto(
        String userName,
        String profileUrl,
        String comment,
        Long userId,
        String createdAt
) {
}
