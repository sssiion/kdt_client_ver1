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


    private String customerId; // 계좌번호
    private String productName;
    private BigDecimal totalAmount;
    private BigDecimal interestRate;
    private LocalDate loanDate; //대출한 날짜
    private LocalDate maturityDate;
    public LoanAccountCreateRequestDto() {};
}
