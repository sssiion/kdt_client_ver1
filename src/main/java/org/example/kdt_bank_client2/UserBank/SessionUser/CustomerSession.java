// Session.java (직원 세션 저장 기능 추가)
package org.example.kdt_bank_client2.UserBank.SessionUser;

import lombok.Getter;
import lombok.Setter;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CashTransactionResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

// dto 저장
@Component
@Setter
@Getter
public class CustomerSession {
    public CustomerResponseDto customerResponseDto;
    public List<AccountResponseDto> accountResponseDtos; // 사용자 계좌 리스트
    public AccountResponseDto accountResponseDto; // 사용자 사용 계좌
    public AccountResponseDto otheracountResponseDto;
    public List<LoanAccountResponseDto>  loanAccountResponseDtos;
    public LoanAccountResponseDto loanAccountResponseDto;
    public CashTransactionResponseDto  cashTransactionResponseDto;

    public void incustomer(CustomerResponseDto dto){
        this.customerResponseDto=dto;
    }
    public CustomerResponseDto getCustomer(){
        return this.customerResponseDto;
    }


    //사용할 계좌 조회 > 계좌 번호
    public void getcustomerAccount(String accountNumber){
        for(AccountResponseDto dto : this.accountResponseDtos){
            if(dto.getAccountNumber().equals(accountNumber)){
                this.accountResponseDto =  dto;
            }
        }
    }
}
