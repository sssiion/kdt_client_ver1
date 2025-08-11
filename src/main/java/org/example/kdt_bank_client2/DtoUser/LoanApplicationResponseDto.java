package org.example.kdt_bank_client2.DtoUser;


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
    private String customerId;
    private String productName;
    private BigDecimal requestedAmount;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private String status;
    private String approvedBy;
    private String rejectionReason;
    private String targetAccountNumber;

}
