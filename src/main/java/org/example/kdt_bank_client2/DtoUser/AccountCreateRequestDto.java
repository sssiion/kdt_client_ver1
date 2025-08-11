package org.example.kdt_bank_client2.DtoUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 계좌 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class AccountCreateRequestDto {

    private String customerId;
    private String productName;
    private BigDecimal amount = BigDecimal.valueOf(0);
    private String productType;
    public AccountCreateRequestDto(String customerId, String productName,String productType){
        this.customerId=customerId;
        this.productName=productName;
        this.productType=productType;

    }



}
