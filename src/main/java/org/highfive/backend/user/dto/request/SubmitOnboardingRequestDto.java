package org.highfive.backend.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.highfive.backend.user.entity.Gender;

public record SubmitOnboardingRequestDto(
        @NotNull
        Long userId,

        @NotNull(message = "작품 선택은 필수입니다.")
        @Size(min = 10, max = 10, message = "총 10개의 작품을 선택해야 합니다.")
        List<Long> selectedContentIds,

        @Min(1900)
        @Max(2025)
        @NotNull
        Integer birthYear,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotNull(message = "이름은 필수입니다.")
        String name
) {
}
