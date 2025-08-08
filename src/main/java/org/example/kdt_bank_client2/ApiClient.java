package org.example.kdt_bank_client2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

@Component
public class ApiClient {
    @Value("${app.server.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final HttpClient CLIENT = HttpClient.newBuilder().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // LocalDateTime 직렬화 설정
        MAPPER.findAndRegisterModules();
    }


    public <T> T post(String path, Object body, TypeReference<T> typeRef) throws Exception {
        String json;
        String contentType;

        // 🔥 새로 추가: String 타입일 때는 JSON이 아닌 plain text로 처리
        if (body instanceof String) {
            json = (String) body;
            contentType = "text/plain";
        } else {
            json = body != null ? MAPPER.writeValueAsString(body) : "";
            contentType = "application/json";
        }


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        // 401 오류일 때 더 구체적인 정보
        if (response.statusCode() == 401) {
            System.err.println("❌ 401 Unauthorized - 인증 관련 문제");
            throw new RuntimeException("서버 인증 오류 (401): " + response.body());
        }

        if (response.body() == null || response.body().trim().isEmpty()) {
            throw new RuntimeException("서버로부터 빈 응답을 받았습니다. 상태 코드: " + response.statusCode());
        }
        return MAPPER.readValue(response.body(), typeRef);
    }

    public  <T> T get(String path, TypeReference<T> typeRef) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(), typeRef);
    }

    public  void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .DELETE()
                .build();
        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
