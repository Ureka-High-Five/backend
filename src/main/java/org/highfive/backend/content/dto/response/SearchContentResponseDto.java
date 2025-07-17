package org.highfive.backend.content.dto.response;

public record SearchContentResponseDto(
        long contentId,
        String thumbnailUrl,
        String title,
        int openYear
) {
}
