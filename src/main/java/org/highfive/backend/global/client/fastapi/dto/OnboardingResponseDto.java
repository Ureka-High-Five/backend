package org.highfive.backend.global.client.fastapi.dto;

import java.util.List;
import java.util.Map;

public record OnboardingResponseDto(
        String vector,
        Map<String, Double> weights
) {
}
