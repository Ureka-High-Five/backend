package org.highfive.backend.content.dto.response;

public record SearchContentResponseDto(
        long contentId,
        String posterUrl,
        String title,
        int openYear
) {
}
