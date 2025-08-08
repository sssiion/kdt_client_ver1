package org.example.kdt_bank_client2.ControllerUser;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.DtoUser.LoanDocsResponseDto;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoanDocsController {

    private final ApiClient api;

    // 전체 목록: GET /api/loan-docs
    public List<LoanDocsResponseDto> listAll() throws Exception {
        return api.get("/api/loan-docs",
                new TypeReference<List<LoanDocsResponseDto>>() {});
    }

    // 단건: GET /api/loan-docs/{id}
    public LoanDocsResponseDto getById(Long id) throws Exception {
        return api.get("/api/loan-docs/" + id,
                new TypeReference<LoanDocsResponseDto>() {});
    }

    // 신청별 목록: GET /api/loan-docs/application/{applicationId}
    public List<LoanDocsResponseDto> listByApplicationId(Long applicationId) throws Exception {
        return api.get("/api/loan-docs/application/" + applicationId,
                new TypeReference<List<LoanDocsResponseDto>>() {});
    }

    // 파일유형 목록: GET /api/loan-docs/file-type/{fileType}
    public List<LoanDocsResponseDto> listByFileType(String fileType) throws Exception {
        return api.get("/api/loan-docs/file-type/" + fileType,
                new TypeReference<List<LoanDocsResponseDto>>() {});
    }

    // 신청별 + 유형: GET /api/loan-docs/application/{applicationId}/file-type/{fileType}
    public List<LoanDocsResponseDto> listByApplicationIdAndType(Long applicationId, String fileType) throws Exception {
        return api.get("/api/loan-docs/application/" + applicationId + "/file-type/" + fileType,
                new TypeReference<List<LoanDocsResponseDto>>() {});
    }

    // 파일명 검색: GET /api/loan-docs/search?fileName=xxx
    public List<LoanDocsResponseDto> searchByFileName(String fileName) throws Exception {
        return api.get("/api/loan-docs/search?fileName=" + fileName,
                new TypeReference<List<LoanDocsResponseDto>>() {});
    }

    // 신청별 개수: GET /api/loan-docs/application/{applicationId}/count
    public Long countByApplication(Long applicationId) throws Exception {
        return api.get("/api/loan-docs/application/" + applicationId + "/count",
                new TypeReference<Long>() {});
    }

    // 업로드: POST multipart /api/loan-docs/upload (applicationId, file, fileType?)
    public LoanDocsResponseDto upload(Long applicationId, Path filePath, String fileType) throws Exception {
        byte[] bytes = Files.readAllBytes(filePath);
        Map<String, String> fields = new HashMap<>();
        fields.put("applicationId", String.valueOf(applicationId));
        if (fileType != null) fields.put("fileType", fileType);

        return api.postMultipart(
                "/api/loan-docs/upload",
                fields,
                "file", // 서버 @RequestParam("file")
                filePath.getFileName().toString(),
                bytes,
                new TypeReference<LoanDocsResponseDto>() {}
        );
    }

    // 삭제(단건): DELETE /api/loan-docs/{id}
    public void delete(Long id) throws Exception {
        api.delete("/api/loan-docs/" + id);
    }

    // 신청별 전체 삭제: DELETE /api/loan-docs/application/{applicationId}
    public void deleteByApplication(Long applicationId) throws Exception {
        api.delete("/api/loan-docs/application/" + applicationId);
    }
}
