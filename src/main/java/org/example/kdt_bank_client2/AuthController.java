package org.example.kdt_bank_client2;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.kdt_bank_client2.DTO.ApiResponse;
import org.example.kdt_bank_client2.DTO.UserLoginDto;
import org.example.kdt_bank_client2.DTO.UserRegisterDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AuthController {
    private UserResponseDto currentUser;

    public boolean register(String userId, String password, String userName, String userPhone) {
        try {
            UserRegisterDto dto = new UserRegisterDto(userId, password, userName, userPhone);
            ApiResponse<UserResponseDto> response = ApiClient.post("/api/users/register", dto,
                    new TypeReference<ApiResponse<UserResponseDto>>() {});

            System.out.println("회원가입 응답: " + response.getMessage());
            return response.isSuccess();
        } catch (Exception e) {
            System.err.println("회원가입 오류: " + e.getMessage());
            return false;
        }
    }

    public boolean login(String userId, String password) {
        try {
            UserLoginDto dto = new UserLoginDto(userId, password);
            ApiResponse<UserResponseDto> response = ApiClient.post("/api/users/login", dto,
                    new TypeReference<ApiResponse<UserResponseDto>>() {});

            if (response.isSuccess()) {
                currentUser = response.getData();
                System.out.println("로그인 성공: " + currentUser.getUserName());
                return true;
            } else {
                System.err.println("로그인 실패: " + response.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("로그인 오류: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            try {
                ApiClient.post("/api/users/logout?userId=" + currentUser.getUserId(), null,
                        new TypeReference<ApiResponse<String>>() {});
                currentUser = null;
                System.out.println("로그아웃 완료");
            } catch (Exception e) {
                System.err.println("로그아웃 오류: " + e.getMessage());
            }
        }
    }

    public UserResponseDto me() {
        return currentUser;
    }
}
