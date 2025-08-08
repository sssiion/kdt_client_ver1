package org.example.kdt_bank_client2.Service;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.Controller.UserController;
import org.example.kdt_bank_client2.DTO.UserDataDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.Session.UserSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserController userController;
    private final UserSession userSession;

    public List<UserDataDto> getStatus() throws Exception {
        List<UserDataDto> u = userController.ClientStatus();
        System.out.println("받은 데이터 개수: " + u.size());
        return u;
    }
    public List<String> getAuth() throws  Exception{
        List<String> A = userController.AuthList();
        return A;
    }

    public void login(String userId, String password, Consumer<UserResponseDto> onSuccess, Consumer<String> onError) {
        if (userId == null || userId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            onError.accept("사용자 ID와 비밀번호를 입력해주세요.");
            return;
        }

        try {
            boolean success = userController.login(userId.trim(), password);
            if (success) {
                UserResponseDto user = userController.me();
                System.out.println("✅ 로그인 성공: " + user.getUserName());
                // 세션에 사용자 정보 저장
                userSession.setCurrentUser(user);
                onSuccess.accept(user);
            } else {
                onError.accept("로그인 실패. 아이디와 비밀번호를 확인해주세요.");
            }
        } catch (Exception e) {
            System.err.println("❌ 로그인 오류: " + e.getMessage());
            onError.accept("로그인 오류: " + e.getMessage());
        }
    }


    public void register( String userName, String userPhone,
                         Runnable onSuccess, Consumer<String> onError) {
        if (userName == null || userName.trim().isEmpty()) {
            onError.accept("사용자 이름을 입력해주세요.");
            return;
        }

        try {
            boolean success = userController.register(
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
        try {
            // 현재 사용자 정보 가져와서 서버에 로그아웃 요청
            UserResponseDto currentUser = userSession.getCurrentUser();
            if (currentUser != null) {
                userController.logout();
                System.out.println("✅ 서버 로그아웃 완료");
            }
        } catch (Exception e) {
            System.err.println("❌ 서버 로그아웃 오류: " + e.getMessage());
        } finally {
            // 항상 세션 정리
            userSession.clearSession();
            System.out.println("✅ 로컬 세션 정리 완료");
        }
    }

    public UserResponseDto getCurrentUser() {
        return userSession.getCurrentUser();
    }
    public boolean isLoggedIn() {
        return userSession.isLoggedIn();
    }


}
