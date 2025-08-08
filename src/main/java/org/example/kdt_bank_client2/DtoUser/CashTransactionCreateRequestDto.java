package org.example.kdt_bank_client2.DtoUser;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 현금 거래 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class CashTransactionCreateRequestDto {

    @NotNull(message = "계좌번호는 필수입니다")
    private Long accountNumber;

    @NotNull(message = "거래 유형은 필수입니다")
    private CashTransaction.TransactionType transactionType;

    @NotNull(message = "거래 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "거래 금액은 0보다 커야 합니다")
    private BigDecimal amount;

    private String note;
}
