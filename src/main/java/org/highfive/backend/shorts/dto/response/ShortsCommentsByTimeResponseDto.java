package org.highfive.backend.shorts.dto.response;

public record ShortsCommentsByTimeResponseDto(
        Long commentId,
        Long time,
        String userName,
        String profileUrl,
        String comment,
        Long userId
) {
}
