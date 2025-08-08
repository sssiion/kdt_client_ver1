package org.example.kdt_bank_client2.DtoUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.LoanApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 대출 신청 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoanApplicationResponseDto {

    private Long applicationId;
    private Long customerId;
    private String customerName;
    private String productName;
    private BigDecimal requestedAmount;
    private LoanApplication.ApplicationStatus status;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private Long approvedBy;
    private String approverName;
    private String rejectionReason;
    private Long targetAccountNumber;

    public LoanApplicationResponseDto(LoanApplication application) {
        this.applicationId = application.getApplicationId();
        this.customerId = application.getCustomerId();
        this.customerName = null; // 별도로 설정 필요
        this.productName = application.getProductName();
        this.requestedAmount = application.getRequestedAmount();
        this.status = application.getStatus();
        this.applicationDate = application.getApplicationDate();
        this.approvalDate = application.getApprovalDate();
        this.approvedBy = application.getApprovedBy();
        this.approverName = null; // 별도로 설정 필요
        this.rejectionReason = application.getRejectionReason();
        this.targetAccountNumber = application.getTargetAccountNumber();
        System.out.println("LoanApplicationResponseDto 생성: 신청ID " + applicationId +
                ", 고객ID: " + customerId +
                ", 상품: " + productName +
                ", 신청금액: " + requestedAmount +
                ", 상태: " + status);
    }
}
