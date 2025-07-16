package org.highfive.backend.shorts.dto.response;

public record ShortsCommentsByTimeResponseDto(
       Long time,
       String username,
       String profileUrl,
       String comment,
       Long userId
) {
}
