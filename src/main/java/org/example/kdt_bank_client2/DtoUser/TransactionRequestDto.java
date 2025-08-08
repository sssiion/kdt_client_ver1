package org.example.kdt_bank_client2.DtoUser;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 거래 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class TransactionRequestDto {

    @NotNull(message = "거래 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "거래 금액은 0보다 커야 합니다")
    private BigDecimal amount;

    private String note;
}

