package org.example.kdt_bank_client2.DTO;

import lombok.Data;

@Data
public class UserLoginDto {

    private String userId;
    private String password;

    public UserLoginDto(String userId, String password) {
        this.userId = userId;
        this.password = password;

    }
}
