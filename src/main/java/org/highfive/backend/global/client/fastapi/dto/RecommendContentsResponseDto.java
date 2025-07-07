package org.highfive.backend.global.client.fastapi.dto;

public record RecommendContentsResponseDto(
        long id,
        String title,
        String thumbnailUrl,
        double similarity
) {
}
