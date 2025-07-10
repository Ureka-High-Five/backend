package org.highfive.backend.global.client.fastapi;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.global.client.fastapi.dto.FastApiOnboardingResponseDto;
import org.highfive.backend.global.client.fastapi.dto.FastApiRecommendResponseDto;
import org.highfive.backend.global.client.fastapi.exception.FastApiErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class FastApiClient {

    private final String fastApiUrl = "http://localhost:8000";
    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    /**
     * 온보딩 화면에서 선택한 컨텐츠를 FastAPI 서버에 전달합니다.
     * FastAPI 서버는 사용자의 초기 벡터를 계산하여 반환합니다.
     *
     * @param contentIds // 온보딩 화면에서 선택한 컨텐츠 id
     * @return // 가중치와 벡터 저장 성공 시 true 아니면 false
     */
    public FastApiOnboardingResponseDto onboarding(final List<Long> contentIds) {
        String url = genUrl("/user/preferences");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Long>> request = new HttpEntity<>(contentIds, headers);

        return executeWithFastApiHandling(() ->
                restTemplate.postForEntity(url, request, FastApiOnboardingResponseDto.class).getBody()
        );
    }

    /**
     * 사용자 아이디를 FastAPI 서버에 전달합니다.
     * FastAPI 서버는 사용자에게 추천할 컨텐츠를 반환합니다.
     *
     * @param vector // 사용자 벡터
     * @return // 추천할 컨텐츠 아이디
     */
    public List<FastApiRecommendResponseDto> getContentsByUserVector(final String vector, final int count) {
        final String url = genUrl("/contents?count=" + count);
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> request = new HttpEntity<>(vector, headers);

        return executeWithFastApiHandling(() ->
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<List<FastApiRecommendResponseDto>>() {}
                ).getBody()
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
        return fastApiUrl + endPoint;
    }
}
