package org.example.kdt_bank_client2.Session;


import lombok.Getter;
import lombok.Setter;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class AccountResponseSession {
    private AccountResponseDto accountResponseDto;


}
