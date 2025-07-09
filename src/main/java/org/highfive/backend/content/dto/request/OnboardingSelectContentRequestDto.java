package org.highfive.backend.content.dto.request;

import java.util.List;

public record OnboardingSelectContentRequestDto(
    List<Long> selectedContentIds
) {
}
