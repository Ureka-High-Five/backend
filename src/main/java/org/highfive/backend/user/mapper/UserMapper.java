package org.highfive.backend.user.mapper;

import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.user.entity.Role;
import org.highfive.backend.user.entity.User;

public class UserMapper {

    public static User from(final KakaoUserResponseDto response) {
        final KakaoUserResponseDto.KakaoProfile profile = response.kakaoAccount().profile();
        return User.builder()
                .kakaoUserId(response.id())
                .name(profile.nickname())
                .profileUrl(profile.profileImageUrl())
                .role(Role.USER)
                .build();
    }
}
