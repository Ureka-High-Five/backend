package org.highfive.backend.content.dto.mapper;

import org.highfive.backend.content.dto.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.entity.Content;

public class ContentMapper {

    public static OnboardingInitContentsResponseDto toOnboardingInitContentsResponseDto(Content content) {
        return new OnboardingInitContentsResponseDto(content.getId(), content.getThumbnailUrl(), content.getTitle());
    }
}
