package org.highfive.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;
import org.highfive.backend.user.entity.UserRole;

public record UpdateUserRoleRequestDto(
        @NotNull
        Long userId,
        @NotNull
        UserRole role
) {
}