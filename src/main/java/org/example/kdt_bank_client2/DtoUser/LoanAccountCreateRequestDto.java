package org.example.kdt_bank_client2.DtoUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// 대출 계좌 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoanAccountCreateRequestDto {


    private Long customerId;
    private String productName;

    private BigDecimal totalAmount;

    private BigDecimal interestRate;

    private LocalDate loanDate;

    private LocalDate maturityDate;
}
