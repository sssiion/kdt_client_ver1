package org.example.kdt_bank_client2.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.DTO.*;
import org.example.kdt_bank_client2.Session.UserSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserController {
    private final UserSession userSession;
    private final ApiClient apiClient;

    public boolean register(String userName, String userPhone) {
        try {
            UserRegisterDto dto = new UserRegisterDto(userName, userPhone);
            ApiResponse<UserResponseDto> response = apiClient.post("/api/users/register", dto,
                    new TypeReference<ApiResponse<UserResponseDto>>() {});

            System.out.println("회원가입 응답: " + response.getMessage());
            return response.isSuccess();
        } catch (Exception e) {
            System.err.println("회원가입 오류: " + e.getMessage());
            return false;
        }
    }
    public List<String> AuthList() throws Exception{
        ApiResponse<List<String>> response =
                apiClient.get("/api/users/Auth", new TypeReference<ApiResponse<List<String>>>() { });

        return response.getData();
    }
    public List<UserDataDto> ClientStatus() throws Exception{
        ApiResponse<List<UserDataDto>> response =
                apiClient.get("/api/users/status", new TypeReference<ApiResponse<List<UserDataDto>>>() { });

        return response.getData();
    }
    public List<UserResponseDto> AllUser() throws Exception {
            ApiResponse<List<UserResponseDto>> response =
                    apiClient.get("/api/users/AllUsers", new TypeReference<ApiResponse<List<UserResponseDto>>>() {});

            return response.getData();

    }
    public boolean login(String userId, String password) {
        try {
            UserLoginDto dto = new UserLoginDto(userId, password);
            ApiResponse<UserResponseDto> response = apiClient.post("/api/users/login", dto,
                    new TypeReference<ApiResponse<UserResponseDto>>() {});

            if (response.isSuccess()) {
                UserResponseDto user = response.getData();
                userSession.setCurrentUser(user); // ← 세션에 저장
                System.out.println("로그인 성공: " + user.getUserName());
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

    public void changeUser(String userId, String change) {
        try {

            ApiResponse<UserResponseDto> response = apiClient.post("/api/users/"+userId+"/change/"+change, null,
                    new TypeReference<ApiResponse<UserResponseDto>>() {});

            userSession.setCurrentUser(response.getData());
        } catch (Exception e) {
            System.err.println("사용자 데이터 변경 오류: " + e.getMessage());

        }
    }

    public void logout() {
        UserResponseDto currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            try {
                // String을 직접 전송 (JSON이 아님)
                apiClient.post("/api/users/logout", currentUser.getUserId(),
                        new TypeReference<ApiResponse<String>>() {});
                currentUser = null;
                System.out.println("로그아웃 완료");
            } catch (Exception e) {
                System.err.println("로그아웃 오류: " + e.getMessage());
            }finally {
                currentUser = null;
            }
        }
    }

    public UserResponseDto me() {
        UserResponseDto currentUser = userSession.getCurrentUser();
        return currentUser;
    }

}
