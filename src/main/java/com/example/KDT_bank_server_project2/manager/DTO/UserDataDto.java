package com.example.KDT_bank_server_project2.manager.DTO;

import com.example.KDT_bank_server_project2.manager.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserDataDto {
    private String userId;
    private String userName;
    private Boolean isOnline;
    private String userPhone;
    private User.UserType userType;
    private User.UserStatus status;

    public UserDataDto(User user){
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.isOnline = user.getIsOnline();
        this.userPhone = user.getUserPhone();
        this.userType = user.getUser_authority();
        this.status = user.getStatus();
        System.out.println("UserDataDto 생성: " + userName +
                ", userType: " + userType +
                ", status: " + status); // ← status 값 확인
    }

}
