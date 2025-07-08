package org.highfive.backend.global.client.fastapi.dto;

import java.util.Map;

public record FastApiOnboardingResponseDto(
        String vector,
        Map<String, Double> weights
) {
}
