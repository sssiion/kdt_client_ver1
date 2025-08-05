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
        // SpringBoot 컨텍스트 초기화
        // headless 모드 비활성화
        System.setProperty("java.awt.headless", "false");

        applicationContext = new SpringApplicationBuilder(KdtBankServerProject2Application.class)
                .headless(false)  // JavaFX와 충돌 방지
                .run();
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
