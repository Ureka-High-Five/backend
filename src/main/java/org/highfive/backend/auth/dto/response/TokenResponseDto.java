package org.highfive.backend.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        boolean isNew
) {
}
