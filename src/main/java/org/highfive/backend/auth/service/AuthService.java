package org.highfive.backend.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.client.KakaoOAuthClient;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.dto.request.OAuthRequestDto;
import org.highfive.backend.auth.dto.request.ReissueRequestDto;
import org.highfive.backend.auth.dto.response.OnboardingResponseDto;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.auth.repository.redis.TokenRedisRepository;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.dto.mapper.UserMapper;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.highfive.backend.auth.service.TokenType.REFRESHTOKEN;
import static org.highfive.backend.global.code.SuccessCode.OK;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final TokenRedisRepository tokenRedisRepository;

    @Transactional
    public Response<?> login(final OAuthRequestDto OAuthRequestDto) {

        final String code = OAuthRequestDto.code();
        final String token = kakaoOAuthClient.requestToken(code).accessToken();
        final KakaoUserResponseDto userInfo = kakaoOAuthClient.requestUser(token);
        final String kakaoUserId = userInfo.id();

        User user = userRepository.findByKakaoUserId(kakaoUserId).orElse(null);

        if (user == null) {
            user = saveUser(userInfo, UserRole.TEMP_USER);
            String nickname = userInfo.kakaoAccount().profile().nickname();
            return onboardingResponse(user.getId(), nickname);
        }

        if (isTempUser(user.getUserRole())) {
            return onboardingResponse(user.getId(), user.getName());
        }

        final List<String> roles = List.of(user.getUserRole().toString());
        return tokenResponse(
                tokenService.generateAccessToken(kakaoUserId, roles),
                tokenService.generateRefreshToken(kakaoUserId, roles)
        );
    }

    public Response<TokenResponseDto> reissue(final ReissueRequestDto reissueRequestDto) {

        final String refreshToken = reissueRequestDto.refreshToken();
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) tokenService.getAuthentication(refreshToken, REFRESHTOKEN);
        final User user = (User) authentication.getPrincipal();

        tokenService.validateToken(refreshToken, REFRESHTOKEN);
        if(!tokenRedisRepository.isRefreshTokenValid(user.getKakaoUserId(), refreshToken)) {
            throw new BusinessException(AuthErrorCode.TOKEN_MISMATCH_ERROR);
        }

        final String renewAccessToken = tokenService.generateAccessToken(user.getKakaoUserId(), List.of(user.getUserRole().toString()));
        return tokenResponse(renewAccessToken, refreshToken);
    }

    public Response<Void> logout(final User user, final HttpServletRequest request) {
        final String accessToken = tokenService.resolveToken(request);
        final long tokenRemainingTime = tokenService.getRemainingTime(accessToken);
        tokenRedisRepository.saveLogoutToken(accessToken, tokenRemainingTime);

        final String kakaoUserId = user.getKakaoUserId();
        tokenRedisRepository.delete(kakaoUserId);

        return new Response<>(OK.getCode(), null, OK.getMessage());
    }

    private User saveUser(final KakaoUserResponseDto userInfo, final UserRole role) {
        final User user = UserMapper.from(userInfo, role);
        return userRepository.save(user);
    }

    private Response<TokenResponseDto> tokenResponse(final String accessToken, final String refreshToken) {
        final TokenResponseDto tokens = new TokenResponseDto(accessToken, refreshToken, false);
        return new Response<>(OK.getCode(), tokens, OK.getMessage());
    }

    private Response<OnboardingResponseDto> onboardingResponse(final long userId, final String nickname) {
        return new Response<>(OK.getCode(), new OnboardingResponseDto(userId, nickname, true), OK.getMessage());
    }

    private boolean isTempUser(final UserRole role) {
        return UserRole.TEMP_USER.equals(role);
    }
}
