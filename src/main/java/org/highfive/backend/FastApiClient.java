package org.highfive.backend;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FastApiClient {

    private final String fastApiUrl = "http://localhost:8000";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 온보딩 화면에서 선택한 컨텐츠를 FastAPI 서버에 전달합니다.
     * FastAPI 서버는 사용자의 초기 가중치와 벡터를 저장합니다.
     *
     * @param contentIds // 온보딩 화면에서 선택한 컨텐츠 id
     * @return // 가중치와 벡터 저장 성공 시 true 아니면 false
     */
    public Boolean onboarding(final List<Integer> contentIds) {
        String url = genUrl("/onboarding");

        HttpHeaders headers = new HttpHeaders();
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
    public List<Integer> getContentsByUserId(final long userId) {
        String url = genUrl("/contents");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);

        ResponseEntity<List<Integer>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    private String genUrl(String endPoint) {
        return fastApiUrl + endPoint;
    }
}
