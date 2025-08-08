package com.example.KDT_bank_server_project2.manager.ControllerUser;

import com.example.KDT_bank_server_project2.manager.EntityUser.LoanDocs;
import com.example.KDT_bank_server_project2.manager.ServiceUser.LoanDocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loan-docs")
@CrossOrigin(origins = "*")
public class LoanDocsController {

    @Autowired
    private LoanDocsService loanDocsService;

    // 대출 서류 업로드
    @PostMapping("/upload")
    public ResponseEntity<LoanDocs> uploadLoanDocument(
            @RequestParam Long applicationId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String fileType) {
        try {
            // 파일 유효성 검사
            if (!loanDocsService.isValidFileType(file.getOriginalFilename())) {
                return ResponseEntity.badRequest().body(null);
            }

            if (!loanDocsService.isValidFileSize(file)) {
                return ResponseEntity.badRequest().body(null);
            }

            LoanDocs uploadedDocs = loanDocsService.uploadLoanDocument(applicationId, file, fileType);
            return ResponseEntity.ok(uploadedDocs);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 모든 대출 서류 조회
    @GetMapping
    public ResponseEntity<List<LoanDocs>> getAllLoanDocs() {
        List<LoanDocs> docsList = loanDocsService.getAllLoanDocs();
        return ResponseEntity.ok(docsList);
    }

    // ID로 대출 서류 조회
    @GetMapping("/{id}")
    public ResponseEntity<LoanDocs> getLoanDocsById(@PathVariable Long id) {
        Optional<LoanDocs> docs = loanDocsService.getLoanDocsById(id);
        return docs.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 대출 신청별 서류 조회
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<LoanDocs>> getLoanDocsByApplicationId(@PathVariable Long applicationId) {
        List<LoanDocs> docsList = loanDocsService.getLoanDocsByApplicationId(applicationId);
        return ResponseEntity.ok(docsList);
    }

    // 파일 유형별 서류 조회
    @GetMapping("/file-type/{fileType}")
    public ResponseEntity<List<LoanDocs>> getLoanDocsByFileType(@PathVariable String fileType) {
        List<LoanDocs> docsList = loanDocsService.getLoanDocsByFileType(fileType);
        return ResponseEntity.ok(docsList);
    }

    // 대출 신청별 특정 파일 유형 서류 조회
    @GetMapping("/application/{applicationId}/file-type/{fileType}")
    public ResponseEntity<List<LoanDocs>> getLoanDocsByApplicationIdAndFileType(
            @PathVariable Long applicationId,
            @PathVariable String fileType) {
        List<LoanDocs> docsList = loanDocsService.getLoanDocsByApplicationIdAndFileType(applicationId, fileType);
        return ResponseEntity.ok(docsList);
    }

    // 파일명으로 서류 검색
    @GetMapping("/search")
    public ResponseEntity<List<LoanDocs>> searchLoanDocsByFileName(@RequestParam String fileName) {
        List<LoanDocs> docsList = loanDocsService.searchLoanDocsByFileName(fileName);
        return ResponseEntity.ok(docsList);
    }

    // 특정 대출 신청의 서류 개수 조회
    @GetMapping("/application/{applicationId}/count")
    public ResponseEntity<Long> getLoanDocsCountByApplicationId(@PathVariable Long applicationId) {
        long count = loanDocsService.getLoanDocsCountByApplicationId(applicationId);
        return ResponseEntity.ok(count);
    }

    // 기간별 서류 조회
    @GetMapping("/date-range")
    public ResponseEntity<List<LoanDocs>> getLoanDocsByUploadDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<LoanDocs> docsList = loanDocsService.getLoanDocsByUploadDateRange(startDate, endDate);
        return ResponseEntity.ok(docsList);
    }

    // 서류 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLoanDocs(@PathVariable Long id) {
        try {
            loanDocsService.deleteLoanDocs(id);
            return ResponseEntity.ok("서류가 삭제되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 삭제 중 오류가 발생했습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 대출 신청별 모든 서류 삭제
    @DeleteMapping("/application/{applicationId}")
    public ResponseEntity<String> deleteLoanDocsByApplicationId(@PathVariable Long applicationId) {
        try {
            loanDocsService.deleteLoanDocsByApplicationId(applicationId);
            return ResponseEntity.ok("해당 신청의 모든 서류가 삭제되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 삭제 중 오류가 발생했습니다.");
        }
    }

    // 파일 다운로드
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            String filePath = loanDocsService.getFilePathForDownload(id);
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                // 파일명 추출
                String filename = path.getFileName().toString();

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 파일 유효성 검사
    @PostMapping("/validate")
    public ResponseEntity<String> validateFile(@RequestParam MultipartFile file) {
        if (!loanDocsService.isValidFileType(file.getOriginalFilename())) {
            return ResponseEntity.badRequest().body("지원하지 않는 파일 형식입니다.");
        }

        if (!loanDocsService.isValidFileSize(file)) {
            return ResponseEntity.badRequest().body("파일 크기가 10MB를 초과합니다.");
        }

        return ResponseEntity.ok("유효한 파일입니다.");
    }
}
