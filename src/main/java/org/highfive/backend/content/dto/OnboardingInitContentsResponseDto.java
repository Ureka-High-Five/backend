package org.highfive.backend.content.dto;

import org.highfive.backend.content.entity.Content;

public record OnboardingInitContentsResponseDto(
        long id,
        String thumbnailUrl,
        String title
) {

    public static OnboardingInitContentsResponseDto of(Content content) {
        return new OnboardingInitContentsResponseDto(content.getId(), content.getThumbnailUrl(), content.getTitle());
    }
}
