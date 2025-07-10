package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OnboardingSelectContentRequestDto(
        @NotNull
        List<Long> selectedContentIds
) {
}
