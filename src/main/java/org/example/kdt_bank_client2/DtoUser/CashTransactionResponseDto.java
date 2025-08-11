package org.example.kdt_bank_client2.DtoUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 현금 거래 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class CashTransactionResponseDto {

    private Long transactionId; //거래 id
    private String accountNumber; // 계좌 번호
    private String otherAccountNumber; // 상대 계좌 번호
    private BigDecimal amount; // 입출금 금액
    private String transactionType; // 입출금 타입
    private LocalDateTime transactionDate; // 입출금 기록 날짜


}
