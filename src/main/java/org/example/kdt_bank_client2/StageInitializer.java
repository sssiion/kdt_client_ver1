package org.example.kdt_bank_client2;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<ChatClientApp.StageReadyEvent>  {
    @Value("${app.ui.title:채팅 클라이언트}")
    private String applicationTitle;

    private final ConfigurableApplicationContext applicationContext;
    private final MainUIController mainUIController;
    private final LoginController loginController;

    public StageInitializer(ConfigurableApplicationContext applicationContext,
                            MainUIController mainUIController,
                            LoginController loginController) {
        this.applicationContext = applicationContext;
        this.mainUIController = mainUIController;
        this.loginController =loginController;
    }

    @Override
    public void onApplicationEvent(ChatClientApp.StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            Scene loginScene = loginController.createLoginScene(stage);

            stage.setTitle(applicationTitle);
            stage.setScene(loginScene);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("UI 초기화 실패", e);
        }
    }
    private Parent createLoginScene() {
        // 추후 실제 UI 구현될 예정
        // 현재는 빈 VBox 반환
        return new javafx.scene.layout.VBox();
    }


}
