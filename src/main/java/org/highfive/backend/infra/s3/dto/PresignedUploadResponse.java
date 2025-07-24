package org.highfive.backend.infra.s3.dto;

public record PresignedUploadResponse(
        String presignedUrl,
        String imageUrl
) {
}
