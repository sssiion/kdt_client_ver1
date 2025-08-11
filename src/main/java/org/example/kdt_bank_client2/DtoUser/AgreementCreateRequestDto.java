package org.example.kdt_bank_client2.DtoUser;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// 약정 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class AgreementCreateRequestDto {


    private String customerId;
    private String productName;
    private LocalDate agreementDate;
    private LocalDate expirationDate;

}
