package org.highfive.backend.content.dto.response;

public record ContentMyReviewResponseDto(
        int rating,
        String review
) {
}
