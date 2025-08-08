// Session.java (직원 세션 저장 기능 추가)
package org.example.kdt_bank_client2.UserBank.SessionUser;

import lombok.Getter;
import lombok.Setter;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.springframework.stereotype.Component;

// dto 저장
@Component
@Setter
@Getter
public class CustomerSession {
    public CustomerResponseDto customerResponseDto;

    public void incustomer(CustomerResponseDto dto){
        this.customerResponseDto=dto;
    }
    public CustomerResponseDto getCustomer(){
        return this.customerResponseDto;
    }

}
