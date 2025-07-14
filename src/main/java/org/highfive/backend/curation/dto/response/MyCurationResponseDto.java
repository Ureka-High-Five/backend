package org.highfive.backend.curation.dto.response;

public record MyCurationResponseDto(
        Long curationId,
        String title,
        String thumbnailUrl
) {
}
