package org.highfive.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.highfive.backend.user.entity.Gender;

public record SubmitOnboardingRequestDto(
        @NotNull
        Long userId,

        @Size(min = 10, max = 10, message = "총 10개의 작품을 선택해야 합니다.")
        List<Long> selectedContentIds,

        @Size(min = 1900, max = 2017)
        @NotNull
        Integer year,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotNull(message = "이름은 필수입니다.")
        String name
) {
}
