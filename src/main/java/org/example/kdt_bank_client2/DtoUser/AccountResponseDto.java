package org.example.kdt_bank_client2.DtoUser;


import javafx.beans.binding.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 계좌 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class AccountResponseDto { //계정 응답

    private String accountNumber; // 계좌번호
    private String customerId; // 사용자 id
    private String productName; // 상품명
    private BigDecimal amount; // 금액
    private String type;
    private LocalDate openingDate;
    private LocalDate closingDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
