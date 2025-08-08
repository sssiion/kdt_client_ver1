package org.example.kdt_bank_client2;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import lombok.RequiredArgsConstructor;

import org.example.kdt_bank_client2.Controller.ChatController;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.Session.ChatRoomSession;
import org.example.kdt_bank_client2.Session.UserSession;
import org.example.kdt_bank_client2.UI.LoginController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<ChatClientApp.StageReadyEvent>  {
    @Value("${app.ui.title:채팅 클라이언트}")
    private String applicationTitle;

    private final UserSession userSession;
    private final ChatRoomSession roomSession;
    private final MainUIController mainUIController;
    private final LoginController loginController;
    // ConfigurableApplicationContext를 필드로 추가해야 함
    private final org.springframework.context.ConfigurableApplicationContext applicationContext;
    private final ApiClient apiClient;


    @Override
    public void onApplicationEvent(ChatClientApp.StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            // 🔥 서버 연결 체크 추가
            if (!performServerHealthCheck()) {
                showServerErrorAndExit(stage);
                return;
            }
            // 세션 상태에 따라 화면 결정
            if (userSession.isLoggedIn()) {
                // 이미 로그인된 상태면 메인 화면으로
                switchToMainUI(stage);
            } else {
                // 로그인되지 않은 상태면 로그인 화면으로
                switchToLogin(stage);
            }


            stage.setTitle(applicationTitle);
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ UI 초기화 실패: " + e.getMessage());
            throw new RuntimeException("UI 초기화 실패", e);

        }
    }

    private void showServerErrorAndExit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("서버 연결 오류");
        alert.setHeaderText("서버에 연결할 수 없습니다");
        alert.setContentText("채팅 서버(localhost:8080)가 실행되고 있지 않습니다.\n" +
                "서버를 먼저 실행한 후 다시 시도해주세요.");

        alert.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(1);
        });

        alert.showAndWait();
        Platform.exit();
        System.exit(1);
    }
    public void switchToLogin(Stage stage) {
        try {
            // 세션 정리
            userSession.clearSession();
            roomSession.clearSession();

            Scene loginScene = loginController.createLoginScene(stage);
            stage.setScene(loginScene);
            System.out.println("🔄 로그인 화면으로 전환");

        } catch (Exception e) {
            System.err.println("❌ 로그인 화면 전환 실패: " + e.getMessage());
            throw new RuntimeException("로그인 화면 전환 실패", e);
        }
    }
    /**
     * 메인 UI로 전환
     */
    public void switchToMainUI(Stage stage) {
        try {
            if (!userSession.isLoggedIn()) {
                System.err.println("❌ 로그인되지 않은 상태에서 메인 UI 접근 시도");
                switchToLogin(stage);
                return;
            }

            Scene mainScene = mainUIController.createMainScene(stage);
            stage.setScene(mainScene);

            UserResponseDto currentUser = userSession.getCurrentUser();
            System.out.println("🔄 메인 UI로 전환 - 사용자: " + currentUser.getUserName());

        } catch (Exception e) {
            System.err.println("❌ 메인 UI 전환 실패: " + e.getMessage());
            // 실패 시 로그인 화면으로 fallback
            switchToLogin(stage);
        }
    }
    /**
     * 채팅방으로 전환
     */
    public void switchToChatRoom(Stage stage, org.example.kdt_bank_client2.DTO.ChatRoomResponseDto room) {
        try {
            if (!userSession.isLoggedIn()) {
                System.err.println("❌ 로그인되지 않은 상태에서 채팅방 접근 시도");
                switchToLogin(stage);
                return;
            }

            // ChatController를 Bean으로 가져와서 사용
            ChatController chatController = applicationContext.getBean(ChatController.class);
            Scene chatScene = chatController.createChatScene(stage, room);
            stage.setScene(chatScene);

            System.out.println("🔄 채팅방으로 전환 - 방: " + room.getRoomName());

        } catch (Exception e) {
            System.err.println("❌ 채팅방 전환 실패: " + e.getMessage());
            // 실패 시 메인 UI로 fallback
            switchToMainUI(stage);
        }
    }
    /**
     * 애플리케이션 종료 시 세션 정리
     */
    public void cleanup() {
        try {
            userSession.clearSession();
            roomSession.clearSession();
            System.out.println("🧹 애플리케이션 종료 - 세션 정리 완료");
        } catch (Exception e) {
            System.err.println("❌ 세션 정리 중 오류: " + e.getMessage());
        }
    }
    // 🔥 개선된 헬스체크 메서드
    private boolean performServerHealthCheck() {
        try {
            System.out.println("🔍 서버 상태 확인 중...");

            // ApiClient를 통한 헬스체크
            apiClient.get("/actuator/health", new TypeReference<Map<String, Object>>() {});

            System.out.println("✅ 서버 연결 확인됨");
            return true;

        } catch (Exception e) {
            System.err.println("❌ 서버 연결 실패: " + e.getMessage());
            return false;
        }
    }




}
