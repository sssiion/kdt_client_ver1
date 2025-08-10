package org.example.kdt_bank_client2.DtoUser;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;



// 대출 서류 업로드 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoanDocsUploadRequestDto {

    //@NotNull(message = "대출 신청 ID는 필수입니다")
    private Long applicationId;

    private String fileType;
    private String description;
}
