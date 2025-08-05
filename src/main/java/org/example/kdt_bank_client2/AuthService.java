package org.example.kdt_bank_client2;

import lombok.Setter;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
@Component
@Setter
public class AuthService {
    private final AuthController authController;

    public AuthService(AuthController authController) {
        this.authController = authController;
    }

    public void login(String userId, String password, Consumer<UserResponseDto> onSuccess, Consumer<String> onError) {
        if (userId == null || userId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            onError.accept("사용자 ID와 비밀번호를 입력해주세요.");
            return;
        }

        try {
            boolean success = authController.login(userId.trim(), password);
            if (success) {
                UserResponseDto user = authController.me();
                System.out.println("✅ 로그인 성공: " + user.getUserName());
                onSuccess.accept(user);
            } else {
                onError.accept("로그인 실패. 아이디와 비밀번호를 확인해주세요.");
            }
        } catch (Exception e) {
            System.err.println("❌ 로그인 오류: " + e.getMessage());
            onError.accept("로그인 오류: " + e.getMessage());
        }
    }

    public void register(String userId, String password, String userName, String userPhone,
                         Runnable onSuccess, Consumer<String> onError) {
        if (userId == null || userId.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                userName == null || userName.trim().isEmpty()) {
            onError.accept("필수 정보를 모두 입력해주세요.");
            return;
        }

        try {
            boolean success = authController.register(
                    userId.trim(),
                    password,
                    userName.trim(),
                    userPhone != null ? userPhone.trim() : ""
            );

            if (success) {
                System.out.println("✅ 회원가입 성공: " + userName);
                onSuccess.run();
            } else {
                onError.accept("회원가입 실패. 입력 정보를 확인해주세요.");
            }
        } catch (Exception e) {
            System.err.println("❌ 회원가입 오류: " + e.getMessage());
            onError.accept("회원가입 오류: " + e.getMessage());
        }
    }

    public void logout() {
        authController.logout();
        System.out.println("✅ 로그아웃 완료");
    }

    public UserResponseDto getCurrentUser() {
        return authController.me();
    }
}
