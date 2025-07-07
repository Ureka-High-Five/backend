package org.highfive.backend.auth.client;

import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.client.dto.response.KakaoUserResponseDto;
import org.highfive.backend.auth.client.dto.response.KakaoTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.reactive.function.BodyInserters.*;

@Slf4j
@Component
public class KakaoOAuthClient {

    private static final String AUTH_BASE_URL = "https://kauth.kakao.com";
    private static final String INFO_BASE_URL = "https://kapi.kakao.com";   // ← 추가
    private static final String TOKEN_URL = "/oauth/token";
    private static final String USER_INFO_URL = "/v2/user/me";
    private final String BEARER = "Bearer ";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient authClient;
    private final WebClient userInfoClient;

    public KakaoOAuthClient() {
        this.authClient = WebClient.builder()
                .baseUrl(AUTH_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.userInfoClient = WebClient.builder()
                .baseUrl(INFO_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE
                )
                .build();
    }

    public KakaoTokenResponseDto requestToken(final String code) {
        return authClient.post()
                .uri(TOKEN_URL)
                .body(fromFormData("grant_type", "authorization_code")
                        .with("client_id",clientId)
                        .with("client_secret",clientSecret)
                        .with("redirect_uri",redirectUri)
                        .with("code",code))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .doOnError(e -> log.error("Kakao token 요청 실패", e))
                .block();
    }

    public KakaoUserResponseDto requestUser(final String accessToken) {
        return userInfoClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_INFO_URL)
                        .queryParam("property_keys",
                                "[\"kakao_account.profile\"]")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(KakaoUserResponseDto.class)
                .doOnError(e -> log.error("Kakao 사용자 정보 요청 실패", e))
                .block();
    }
}
