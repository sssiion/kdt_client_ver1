package org.example.kdt_bank_client2.DtoUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.LoanAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 대출 계좌 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoanAccountResponseDto {

    private String loanId;
    private Long customerId;
    private String customerName;
    private String productName;
    private BigDecimal totalAmount;
    private BigDecimal repaymentAmount;
    private BigDecimal remainingBalance;
    private BigDecimal interestRate;
    private LocalDate loanDate;
    private LocalDate maturityDate;
    private LoanAccount.LoanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LoanAccountResponseDto(LoanAccount loanAccount) {
        this.loanId = loanAccount.getLoanId();
        this.customerId = loanAccount.getCustomerId();
        this.customerName = null; // 별도로 설정 필요
        this.productName = loanAccount.getProductName();
        this.totalAmount = loanAccount.getTotalAmount();
        this.repaymentAmount = loanAccount.getRepaymentAmount();
        this.remainingBalance = loanAccount.getTotalAmount().subtract(loanAccount.getRepaymentAmount());
        this.interestRate = loanAccount.getInterestRate();
        this.loanDate = loanAccount.getLoanDate();
        this.maturityDate = loanAccount.getMaturityDate();
        this.status = loanAccount.getStatus();
        this.createdAt = loanAccount.getCreatedAt();
        this.updatedAt = loanAccount.getUpdatedAt();
        System.out.println("LoanAccountResponseDto 생성: 대출ID " + loanId +
                ", 고객ID: " + customerId +
                ", 대출금액: " + totalAmount +
                ", 잔액: " + remainingBalance +
                ", 상태: " + status);
    }
}
