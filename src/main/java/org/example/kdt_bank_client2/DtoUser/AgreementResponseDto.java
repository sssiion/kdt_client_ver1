package org.example.kdt_bank_client2.DtoUser;


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
public class AgreementResponseDto { // 계약 응답

    private String agreementId;
    private String customerId; // 사용자 아이디
    private String productName; // 상품명
    private LocalDate agreementDate; //
    private LocalDate expirationDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
