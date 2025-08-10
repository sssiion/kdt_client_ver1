package org.example.kdt_bank_client2.UserBank.SessionUser;

import lombok.Getter;
import lombok.Setter;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Setter
@Getter
public class ProductSession {
    public List<ProductResponseDto> productResponseDtos;

}
