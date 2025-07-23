package org.highfive.backend.slang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class ModerationApiSlangFilterService implements SlangFilterService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Override
    public boolean filteringSlang(String content) {
        String json = buildJson(content);
        HttpRequest request = buildRequest(json);
        HttpResponse<String> response = sendRequest(request);

        log.info("content = {}", content);
        return findSlang(response.body());
    }

    private static String buildJson(String content) {
        return String.format("""
            {
              "input": %s
            }
            """, "\"" + content.replace("\"", "\\\"") + "\"");
    }

    private HttpRequest buildRequest(String json) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/moderations"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.info("Moderation API 중 호출 오류 발생");
            throw new RuntimeException(e);
        }
        return response;
    }

    private boolean findSlang(String body) {
        ObjectMapper mapper = new ObjectMapper();
        log.info("body = {}", body);
        boolean isSlang;
        try {
            JsonNode root = mapper.readTree(body);
            if (root.get("error") != null) {
                throw new RuntimeException(String.valueOf(root.get("error").get("message")));
            }
            isSlang = root
                    .get("results")
                    .get(0)
                    .get("flagged") // unsafe일 경우 true
                    .asBoolean();
        } catch (JsonProcessingException e) {
            log.info("body 파싱 중 오류가 발생했습니다");
            throw new RuntimeException(e);
        }
        return isSlang;
    }
}
