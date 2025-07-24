package org.highfive.backend.user.dto.response;

import org.highfive.backend.user.entity.UserRole;

public record SearchUserResponseDto(
        Long userId,
        String userName,
        String profileUrl, // 사용자의 프로필 사진
        String email,      // 사용자의 이메일 주소
        UserRole role
) {
}
