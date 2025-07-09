package org.highfive.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthRequestDto(

        @NotBlank
        String code
) {
}
