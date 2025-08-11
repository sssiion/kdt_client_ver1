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

    private String fromAccountNumber="";
    private String toAccountNumber="";
    private String amount;

    public TransferRequestDto(){}

}
