package org.highfive.backend.content.dto.response;

public record SearchContentResponseDto(
        long contentId,
        String postUrl,
        String title,
        int year
) {
}
