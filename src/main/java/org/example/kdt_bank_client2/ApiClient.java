package org.example.kdt_bank_client2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;


public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newBuilder().build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // LocalDateTime 직렬화 설정
        MAPPER.findAndRegisterModules();
    }

    public static <T> T post(String path, Object body, Class<T> clazz) throws Exception {
        String json = body != null ? MAPPER.writeValueAsString(body) : "";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(), clazz);
    }

    public static <T> T post(String path, Object body, TypeReference<T> typeRef) throws Exception {
        String json = body != null ? MAPPER.writeValueAsString(body) : "";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(), typeRef);
    }

    public static <T> T get(String path, TypeReference<T> typeRef) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(), typeRef);
    }

    public static void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
