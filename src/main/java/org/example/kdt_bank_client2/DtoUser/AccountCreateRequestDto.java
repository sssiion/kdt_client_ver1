package org.example.kdt_bank_client2.DtoUser;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// 계좌 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class AccountCreateRequestDto {

    @NotNull(message = "고객 ID는 필수입니다")
    private Long customerId;

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    private BigDecimal amount;
    private LocalDate openingDate;
    private LocalDate closingDate;

    @NotBlank(message = "상품 유형은 필수입니다")
    private String productType;
}
