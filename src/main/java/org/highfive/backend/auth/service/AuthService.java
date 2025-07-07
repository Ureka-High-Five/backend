package org.highfive.backend.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.client.KakaoOAuthClient;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.controller.dto.request.OAuthRequestDto;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;

    @Transactional
    public void login(final OAuthRequestDto OAuthRequestDto) {
        final String code = OAuthRequestDto.code();
        final String token = kakaoOAuthClient.requestToken(code).accessToken();
        final KakaoUserResponseDto userInfo = kakaoOAuthClient.requestUser(token);
        final String kakaoUserId = userInfo.id();

        if(userRepository.existsByKakaoUserId(kakaoUserId)) {
            // 토큰 발급
        }

        saveUser(userInfo);
    }

    private void saveUser(final KakaoUserResponseDto userInfo) {
        final User user = User.from(userInfo);
        userRepository.save(user);
    }
}
