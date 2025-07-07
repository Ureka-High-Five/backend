package org.highfive.backend.global.client.fastapi;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.global.client.fastapi.dto.RecommendContentsResponseDto;
import org.highfive.backend.global.code.ErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class FastApiClient {

    private final String fastApiUrl = "http://localhost:8000";
    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    /**
     * 온보딩 화면에서 선택한 컨텐츠를 FastAPI 서버에 전달합니다.
     * FastAPI 서버는 사용자의 초기 가중치와 벡터를 저장합니다.
     *
     * @param contentIds // 온보딩 화면에서 선택한 컨텐츠 id
     * @return // 가중치와 벡터 저장 성공 시 true 아니면 false
     */
    public Boolean onboarding(final List<Integer> contentIds) {
        String url = genUrl("/user/preferences");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Integer>> request = new HttpEntity<>(contentIds, headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);

        return response.getBody();
    }

    /**
     * 사용자 아이디를 FastAPI 서버에 전달합니다.
     * FastAPI 서버는 사용자에게 추천할 컨텐츠를 반환합니다.
     *
     * @param userId // 사용자 아이디
     * @return // 추천할 컨텐츠 아이디
     */
    public List<RecommendContentsResponseDto> getContentsByUserId(final long userId) {
        String url = genUrl("/contents");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);

        try {
            ResponseEntity<List<RecommendContentsResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody();

        } catch (ResourceAccessException e) {
            log.error("FastAPI 서버에 접근할 수 없습니다.", e);
            throw new BusinessException(ErrorCode.FAST_API_ERROR);
        }
    }

    private String genUrl(final String endPoint) {
        return fastApiUrl + endPoint;
    }
}
