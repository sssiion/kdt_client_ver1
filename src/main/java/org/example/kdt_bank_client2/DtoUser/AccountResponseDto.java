package org.example.kdt_bank_client2.DtoUser;


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
public class AccountResponseDto {

    private Long accountNumber;
    private Long customerId;
    private String customerName;
    private String productName;
    private BigDecimal amount;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String status;
    private String productType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
