package org.highfive.backend.global.client.fastapi.dto.request;

import java.util.Map;

public record FastApiOnboardingRequestDto(
        long userId,
        Map<String, Integer> genreCount
) {
}
