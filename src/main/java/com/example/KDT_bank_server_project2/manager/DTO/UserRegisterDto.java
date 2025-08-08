package com.example.KDT_bank_server_project2.manager.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRegisterDto {
    // 🔥 수정: 서버에서는 userId, password 필드가 없음
    // 서버에서는 userName, userPhone만 받음
    private String userName;
    private String userPhone;

    public UserRegisterDto(String userName, String userPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
    }
}

