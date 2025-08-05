package org.example.kdt_bank_client2.DTO;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String userId;
    private String password;
    private String userName;
    private String userPhone;

    public UserRegisterDto(String userId, String password, String userName, String userPhone) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.userPhone = userPhone;

    }

}