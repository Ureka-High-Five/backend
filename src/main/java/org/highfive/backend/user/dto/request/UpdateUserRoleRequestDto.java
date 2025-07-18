package org.highfive.backend.user.dto.request;

import org.highfive.backend.user.entity.UserRole;

public record UpdateUserRoleRequestDto(
        Long userId,
        UserRole role
) {
}