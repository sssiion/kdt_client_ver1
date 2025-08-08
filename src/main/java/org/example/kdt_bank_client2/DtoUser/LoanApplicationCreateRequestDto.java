package org.example.kdt_bank_client2.DtoUser;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 대출 신청 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoanApplicationCreateRequestDto {

    @NotNull(message = "고객 ID는 필수입니다")
    private Long customerId;

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    @NotNull(message = "신청 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "신청 금액은 0보다 커야 합니다")
    private BigDecimal requestedAmount;

    private Long targetAccountNumber;
}

