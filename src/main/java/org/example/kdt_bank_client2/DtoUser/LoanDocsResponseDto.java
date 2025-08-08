package org.example.kdt_bank_client2.DtoUser;

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

}
