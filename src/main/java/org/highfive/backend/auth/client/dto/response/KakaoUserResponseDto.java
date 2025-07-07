package org.highfive.backend.auth.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponseDto(
        String id,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {
        public record KakaoAccount(
                KakaoProfile profile
        ) {}

        public record KakaoProfile(
                String nickname,

                @JsonProperty("profile_image_url")
                String profileImageUrl
        ) {}
}
