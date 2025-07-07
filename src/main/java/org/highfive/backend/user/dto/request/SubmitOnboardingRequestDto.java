package org.highfive.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SubmitOnboardingRequestDto(
        @NotNull(message = "userId는 필수입니다.")
        Long userId,

        @NotNull(message = "selectedContentIds는 필수입니다.")
        @Size(min = 10, max = 10, message = "최소 하나 이상의 콘텐츠를 선택해야 합니다.")
        List<Integer> selectedContentIds,

        @NotNull(message = "나이는 필수입니다.")
        Integer age,

        @NotNull(message = "성별은 필수입니다.")
        String gender,

        @NotNull(message = "이름은 필수입니다.")
        String name
) {}
