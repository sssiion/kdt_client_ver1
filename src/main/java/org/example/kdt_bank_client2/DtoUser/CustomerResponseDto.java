package org.example.kdt_bank_client2.DtoUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 고객 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class CustomerResponseDto {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String residentNumber;

}
