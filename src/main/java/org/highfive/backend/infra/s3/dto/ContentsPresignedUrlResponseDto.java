package org.highfive.backend.infra.s3.dto;

public record ContentsPresignedUrlResponseDto(
        String imagePresignedUrl,
        String imageUrl,
        String shortsPresignedUrl,
        String shortsUrl,
        String videoPresignedUrl,
        String videoUrl,
        String uuid
) {
}
