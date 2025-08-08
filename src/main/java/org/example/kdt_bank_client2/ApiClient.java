package org.example.kdt_bank_client2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

        // 🔥 String일 때는 text/plain, 그 외엔 application/json
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

        if (response.statusCode() == 401) {
            System.err.println("❌ 401 Unauthorized - 인증 관련 문제");
            throw new RuntimeException("서버 인증 오류 (401): " + response.body());
        }
        if (response.body() == null || response.body().trim().isEmpty()) {
            throw new RuntimeException("서버로부터 빈 응답을 받았습니다. 상태 코드: " + response.statusCode());
        }
        return MAPPER.readValue(response.body(), typeRef);
    }

    public <T> T get(String path, TypeReference<T> typeRef) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(), typeRef);
    }

    public void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .DELETE()
                .build();
        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }

    // ====== 추가: PATCH 요청 ======
    public <T> T patch(String path, Object body, TypeReference<T> typeRef) throws Exception {
        String json = (body != null) ? MAPPER.writeValueAsString(body) : "";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            System.err.println("❌ 401 Unauthorized - 인증 관련 문제");
            throw new RuntimeException("서버 인증 오류 (401): " + response.body());
        }
        if (response.body() == null || response.body().trim().isEmpty()) {
            throw new RuntimeException("서버로부터 빈 응답을 받았습니다. 상태 코드: " + response.statusCode());
        }
        return MAPPER.readValue(response.body(), typeRef);
    }

    // ====== 추가: multipart/form-data 업로드 ======
    public <T> T postMultipart(
            String path,
            Map<String, String> fields,     // 텍스트 필드 (없으면 null)
            String fileFieldName,           // 서버 @RequestParam 이름 (예: "file")
            String filename,                // 예: "income.pdf"
            byte[] fileBytes,
            TypeReference<T> typeRef
    ) throws Exception {
        String boundary = "----JavaBoundary" + System.currentTimeMillis();

        // 텍스트 파트
        StringBuilder sb = new StringBuilder();
        if (fields != null) {
            for (Map.Entry<String, String> e : fields.entrySet()) {
                sb.append("--").append(boundary).append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"\r\n\r\n");
                sb.append(e.getValue()).append("\r\n");
            }
        }

        // 파일 파트 헤더
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"").append(fileFieldName)
                .append("\"; filename=\"").append(filename).append("\"\r\n");
        sb.append("Content-Type: application/octet-stream\r\n\r\n");

        byte[] header = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArrays(List.of(header, fileBytes, footer)))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            System.err.println("❌ 401 Unauthorized - 인증 관련 문제");
            throw new RuntimeException("서버 인증 오류 (401): " + response.body());
        }
        if (response.body() == null || response.body().trim().isEmpty()) {
            throw new RuntimeException("서버로부터 빈 응답을 받았습니다. 상태 코드: " + response.statusCode());
        }
        return MAPPER.readValue(response.body(), typeRef);
    }
}
