package org.highfive.backend.shorts.dto.response;

public record ShortsCommentsByIdResponseDto(
        Long commentId,
        String userName,
        String profileUrl,
        String comment,
        Long userId,
        String createdAt
) {
}
