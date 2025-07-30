package org.highfive.backend.infra.s3.dto;

public record CurationPresignedUrlResponseDto(
        String presignedUrl,
        String imageUrl
) {
}
