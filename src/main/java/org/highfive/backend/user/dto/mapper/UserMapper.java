package org.highfive.backend.user.dto.mapper;

import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.user.dto.response.UserInfoResponseDto;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.entity.User;

public class UserMapper {

    public static User from(final KakaoUserResponseDto response, final UserRole userRole, final String email) {
        final KakaoUserResponseDto.KakaoProfile profile = response.kakaoAccount().profile();
        return User.builder()
                .email(email)
                .kakaoUserId(response.id())
                .name(profile.nickname())
                .profileUrl(profile.profileImageUrl())
                .userRole(userRole)
                .build();
    }

    public static UserInfoResponseDto from(final User user) {
        return new UserInfoResponseDto(user.getId(), user.getName(), user.getEmail(), user.getUserRole().name(), user.getProfileUrl());
    }
}
