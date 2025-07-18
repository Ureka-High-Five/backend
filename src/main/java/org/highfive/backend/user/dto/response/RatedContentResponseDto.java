package org.highfive.backend.user.dto.response;

public record RatedContentResponseDto(
        long id,
        String thumbnailUrl,
        String title,
        String review,
        int rating
) {
}
