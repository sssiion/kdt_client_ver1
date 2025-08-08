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
public class AgreementResponseDto {

    private Long agreementId;
    private Long customerId;
    private String customerName;
    private String productName;
    private LocalDate agreementDate;
    private LocalDate expirationDate;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
