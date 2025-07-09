package org.highfive.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequestDto(
        @NotBlank
        String refreshToken
) {
}
