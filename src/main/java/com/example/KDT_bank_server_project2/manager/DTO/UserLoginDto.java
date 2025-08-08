package com.example.KDT_bank_server_project2.manager.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor

public class UserLoginDto {

    private String userId;
    private String password;


}
