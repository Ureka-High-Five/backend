package org.highfive.backend.shorts.dto.response;

public record CreateShortsCommentResponseDto(
        Long commentId,
        String comment,
        Long time,
        String userName,
        String profileUrl
) {
}
