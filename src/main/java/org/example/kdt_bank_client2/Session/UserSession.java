package org.example.kdt_bank_client2.Session;

import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;
import org.example.kdt_bank_client2.DTO.UserDataDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class UserSession {
    private UserResponseDto currentUser;
    private UserDataDto currentUserData;


    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void clearSession() {
        this.currentUser = null;
    }
}
