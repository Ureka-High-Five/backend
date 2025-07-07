package org.highfive.backend.auth.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KakaoOAuthClientConfig {

    private final String AUTH_BASE_URL = "https://kauth.kakao.com";
    private final String INFO_BASE_URL = "https://kapi.kakao.com";

    @Bean(name = "kakaoAuthClient")
    public WebClient kakaoAuthClient() {
        return createWebClient(AUTH_BASE_URL);
    }

    @Bean(name = "kakaoUserInfoClient")
    public WebClient kakaoUserInfoClient() {
        return createWebClient(INFO_BASE_URL);
    }

    private WebClient createWebClient(final String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
}
