package org.example.kdt_bank_client2.DtoUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.Agreement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 약정 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class AgreementResponseDto {

    private Long agreementId;
    private Long customerId;
    private String customerName;
    private String productName;
    private LocalDate agreementDate;
    private LocalDate expirationDate;
    private Agreement.AgreementStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AgreementResponseDto(Agreement agreement) {
        this.agreementId = agreement.getAgreementId();
        this.customerId = agreement.getCustomerId();
        this.customerName = null; // 별도로 설정 필요
        this.productName = agreement.getProductName();
        this.agreementDate = agreement.getAgreementDate();
        this.expirationDate = agreement.getExpirationDate();
        this.status = agreement.getStatus();
        this.note = agreement.getNote();
        this.createdAt = agreement.getCreatedAt();
        this.updatedAt = agreement.getUpdatedAt();
        System.out.println("AgreementResponseDto 생성: 약정ID " + agreementId +
                ", 고객ID: " + customerId +
                ", 상품: " + productName +
                ", 체결일: " + agreementDate +
                ", 만료일: " + expirationDate +
                ", 상태: " + status);
    }
}
