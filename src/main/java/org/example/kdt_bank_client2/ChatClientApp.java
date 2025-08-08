package org.example.kdt_bank_client2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class ChatClientApp extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        // 서버 연결 체크 먼저 수행
        if (!checkServerConnection()) {
            // 서버 연결 실패 시 애플리케이션 종료
            showErrorDialogAndExit("서버에 연결할 수 없습니다.\n서버가 실행 중인지 확인해주세요.");
            return;
        }


        // SpringBoot 컨텍스트 초기화
        // headless 모드 비활성화
        System.setProperty("java.awt.headless", "false");

        applicationContext = new SpringApplicationBuilder(KdtBankServerProject2Application.class)
                .headless(false)  // JavaFX와 충돌 방지
                .run();
    }
    private void showErrorDialogAndExit(String message) {
        // JavaFX가 아직 초기화되지 않았으므로 Swing Dialog 사용
        javax.swing.JOptionPane.showMessageDialog(null,
                message, "연결 오류", javax.swing.JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
    private boolean checkServerConnection() {
        try {
            System.out.println("서버 연결 확인 중...");

            // 🔥 포트 연결만 확인 (HTTP 요청 없음)
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress("localhost", 8080), 3000);
                System.out.println("✅ 서버 포트 8080 연결 가능");
                return true;
            }

        } catch (java.net.ConnectException e) {
            System.err.println("❌ 서버 연결 거부: 서버가 실행되지 않았습니다.");
            return false;
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("❌ 서버 연결 시간 초과");
            return false;
        } catch (Exception e) {
            System.err.println("❌ 서버 연결 실패: " + e.getMessage());
            return false;
        }
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 먼저 간단한 콘솔 테스트로 시작
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
    }
    @Override
    public void stop() throws Exception {
        // 애플리케이션 종료 시 Spring 컨텍스트 정리
        if (applicationContext != null) {
            // 세션 정리
            try {
                StageInitializer stageInitializer = applicationContext.getBean(StageInitializer.class);
                stageInitializer.cleanup();
            } catch (Exception e) {
                System.err.println("세션 정리 중 오류: " + e.getMessage());
            }

            applicationContext.close();
        }
        Platform.exit();
    }

    // Stage 준비 완료 이벤트
    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}
