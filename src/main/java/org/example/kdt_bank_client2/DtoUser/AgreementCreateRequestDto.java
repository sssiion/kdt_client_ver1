package org.example.kdt_bank_client2.DtoUser;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "고객 ID는 필수입니다")
    private Long customerId;

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    private LocalDate agreementDate;

    @NotNull(message = "만료일은 필수입니다")
    private LocalDate expirationDate;

    private String note;
}
