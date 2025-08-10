package org.example.kdt_bank_client2.DtoUser;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 이체 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class TransferRequestDto {

    //@NotNull(message = "출금 계좌번호는 필수입니다")
    private String fromAccountNumber;

    //@NotNull(message = "입금 계좌번호는 필수입니다")
    private String toAccountNumber;

    //@NotNull(message = "이체 금액은 필수입니다")
    //@DecimalMin(value = "0.01", message = "이체 금액은 0보다 커야 합니다")
    private BigDecimal amount;

    public TransferRequestDto(){}

}
