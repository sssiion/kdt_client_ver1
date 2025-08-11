package org.example.kdt_bank_client2.DtoUser;


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
    private String customerId;
    private String productName;
    private BigDecimal totalAmount;
    private BigDecimal repaymentAmount;

    private BigDecimal interestRate;
    private LocalDate loanDate;
    private LocalDate maturityDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
