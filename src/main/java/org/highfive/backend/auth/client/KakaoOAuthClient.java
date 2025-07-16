package org.highfive.backend.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.client.dto.response.KakaoTokenResponseDto;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.highfive.backend.auth.exception.AuthErrorCode.KAKAO_TOKEN_ERROR;
import static org.highfive.backend.auth.exception.AuthErrorCode.KAKAO_USERINFO_ERROR;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final String TOKEN_URL = "/oauth/token";
    private final String USER_INFO_URL = "/v2/user/me";
    private final String BEARER = "Bearer ";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient kakaoAuthClient;
    private final WebClient kakaoUserInfoClient;

    public KakaoTokenResponseDto requestToken(final String code) {
        return handleResponse(
                kakaoAuthClient.post()
                        .uri(TOKEN_URL)
                        .body(fromFormData("grant_type", "authorization_code")
                                .with("client_id", clientId)
                                .with("client_secret", clientSecret)
                                .with("redirect_uri", redirectUri)
                                .with("code", code))
                        .retrieve(),
                KAKAO_TOKEN_ERROR
        ).bodyToMono(KakaoTokenResponseDto.class)
                .block();
    }

    public KakaoUserResponseDto requestUser(final String accessToken) {
        return handleResponse(
                kakaoUserInfoClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(USER_INFO_URL)
                                .queryParam("property_keys", "[\"kakao_account.profile\", \"kakao_account.email\"]")
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                        .retrieve(),
                KAKAO_USERINFO_ERROR
        ).bodyToMono(KakaoUserResponseDto.class)
                .block();
    }

    private WebClient.ResponseSpec handleResponse(final WebClient.ResponseSpec responseSpec, final AuthErrorCode errorCode) {
        return responseSpec.onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(String.class)
                        .doOnNext(body -> log.error("[{}] {} 응답: {}", errorCode.getCode(), errorCode.getMessage(), body))
                        .thenReturn(new BusinessException(errorCode))
        );
    }
}