package org.example.kdt_bank_client2.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    // 🔥 수정: 서버에서는 userId, password 필드가 없음
    // 서버에서는 userName, userPhone만 받음
    private String userName;
    private String userPhone;


}
