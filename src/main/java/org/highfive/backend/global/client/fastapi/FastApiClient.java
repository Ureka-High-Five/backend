package org.highfive.backend.global.client.fastapi;

import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.global.client.fastapi.dto.request.FastApiOnboardingRequestDto;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiOnboardingResponseDto;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiVectorFromGenresDto;
import org.highfive.backend.global.client.fastapi.exception.FastApiErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FastApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.host}")
    private String fastApiHost;

    @Value("${fastapi.port}")
    private String fastApiPort;

    private HttpHeaders headers = new HttpHeaders();

    public FastApiVectorFromGenresDto vectorFromGenres(final List<String> genres) {
        final String url = genUrl("/embedding-by-genre");

        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<List<String>> request = new HttpEntity<>(genres, headers);

        return executeWithFastApiHandling(() ->
                restTemplate.postForEntity(url, request, FastApiVectorFromGenresDto.class).getBody());
    }

    public FastApiOnboardingResponseDto onboardingSubmit(final long userId, final Map<String, Integer> genreCount) {
        String url = genUrl("/user/preferences");

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FastApiOnboardingRequestDto> request = new HttpEntity<>(new FastApiOnboardingRequestDto(userId, genreCount), headers);

        return executeWithFastApiHandling(() ->
                restTemplate.postForEntity(url, request, FastApiOnboardingResponseDto.class).getBody()
        );
    }

    private <T> T executeWithFastApiHandling(final FastApiCall<T> apiCall) {
        try {
            final T result = apiCall.call();
            if (result == null) {
                log.error("FastAPI 응답 바디가 null입니다.");
                throw new BusinessException(FastApiErrorCode.FAST_API_RESPONSE_NULL);
            }
            return result;
        } catch (HttpStatusCodeException e) {
            log.error("FastAPI 응답 오류 - 상태 코드: {}, 응답 바디: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException(FastApiErrorCode.FAST_API_RESPONSE_ERROR);
        } catch (ResourceAccessException e) {
            log.error("FastAPI 서버에 접근할 수 없습니다.", e);
            throw new BusinessException(FastApiErrorCode.FAST_API_CONNECTION_ERROR);
        } catch (RestClientException e) {
            log.error("FastAPI 호출 중 알 수 없는 오류 발생", e);
            throw new BusinessException(FastApiErrorCode.FAST_API_ERROR);
        }
    }

    private String genUrl(final String endPoint) {
        return fastApiHost + ":" + fastApiPort + endPoint;
    }
}
