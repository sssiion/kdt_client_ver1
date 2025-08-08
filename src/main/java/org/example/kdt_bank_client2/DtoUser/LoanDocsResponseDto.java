package org.example.kdt_bank_client2.DtoUser;

import com.example.KDT_bank_server_project2.manager.EntityUser.LoanDocs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 대출 서류 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoanDocsResponseDto {

    private Long docId;
    private Long applicationId;
    private String filePath;
    private String fileName;
    private String fileType;
    private LocalDateTime uploadDate;
    private String downloadUrl;

    public LoanDocsResponseDto(LoanDocs loanDocs) {
        this.docId = loanDocs.getDocId();
        this.applicationId = loanDocs.getApplicationId();
        this.filePath = loanDocs.getFilePath();
        this.fileName = loanDocs.getFileName();
        this.fileType = loanDocs.getFileType();
        this.uploadDate = loanDocs.getUploadDate();
        this.downloadUrl = "/api/loan-docs/" + loanDocs.getDocId() + "/download";
        System.out.println("LoanDocsResponseDto 생성: 서류ID " + docId +
                ", 신청ID: " + applicationId +
                ", 파일명: " + fileName +
                ", 파일유형: " + fileType +
                ", 업로드일: " + uploadDate);
    }
}
