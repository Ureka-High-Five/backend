package org.highfive.backend.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.client.KakaoOAuthClient;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.dto.request.ReissueRequestDto;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.jwt.JwtUtils;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.Role;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.highfive.backend.global.code.SuccessCode.OK;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public Response<?> login(final OAuthRequestDto OAuthRequestDto) {

        final String code = OAuthRequestDto.code();
        final String token = kakaoOAuthClient.requestToken(code).accessToken();
        final KakaoUserResponseDto userInfo = kakaoOAuthClient.requestUser(token);
        final String kakaoUserId = userInfo.id();

        if (!userRepository.existsByKakaoUserId(kakaoUserId)) {
            saveUser(userInfo);
            final String nickname = userInfo.kakaoAccount().profile().nickname();
            return nicknameResponse(nickname);
        }

        final List<String> roles = List.of(Role.USER.toString());
        return tokenResponse(jwtUtils.generateAccessToken(kakaoUserId, roles), jwtUtils.generateRefreshToken(kakaoUserId, roles));
    }

    public Response<TokenResponseDto> reissue(final ReissueRequestDto reissueRequestDto) {

        final String refreshToken = reissueRequestDto.refreshToken();
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) jwtUtils.getAuthentication(refreshToken);
        final User user = (User) authentication.getPrincipal();

        jwtUtils.validateToken(refreshToken);
        final String renewAccessToken = jwtUtils.generateAccessToken(user.getKakaoUserId(), List.of(user.getRole().toString()));

        return tokenResponse(renewAccessToken, refreshToken);
    }

    private void saveUser(final KakaoUserResponseDto userInfo) {
        final User user = User.from(userInfo);
        userRepository.save(user);
    }

    private Response<TokenResponseDto> tokenResponse(final String accessToken, final String refreshToken) {
        final TokenResponseDto tokens = new TokenResponseDto(accessToken, refreshToken);
        return new Response<>(OK.getCode(), tokens, OK.getMessage());
    }

    private Response<String> nicknameResponse(final String nickname) {
        return new Response<>(OK.getCode(), nickname, OK.getMessage());
    }
}
