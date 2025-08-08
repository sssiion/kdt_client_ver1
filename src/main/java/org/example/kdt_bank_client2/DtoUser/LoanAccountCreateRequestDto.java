package org.example.kdt_bank_client2.DtoUser;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "고객 ID는 필수입니다")
    private Long customerId;

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    @NotNull(message = "대출 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "대출 금액은 0보다 커야 합니다")
    private BigDecimal totalAmount;

    @NotNull(message = "이자율은 필수입니다")
    @DecimalMin(value = "0.01", message = "이자율은 0보다 커야 합니다")
    private BigDecimal interestRate;

    private LocalDate loanDate;

    @NotNull(message = "만기일은 필수입니다")
    private LocalDate maturityDate;
}
