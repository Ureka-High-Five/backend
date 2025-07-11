package org.highfive.backend.user.dto.mapper;

import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.entity.User;

public class UserMapper {

    public static User from(final KakaoUserResponseDto response, final UserRole userRole) {
        final KakaoUserResponseDto.KakaoProfile profile = response.kakaoAccount().profile();
        return User.builder()
                .kakaoUserId(response.id())
                .name(profile.nickname())
                .profileUrl(profile.profileImageUrl())
                .userRole(userRole)
                .build();
    }
}
