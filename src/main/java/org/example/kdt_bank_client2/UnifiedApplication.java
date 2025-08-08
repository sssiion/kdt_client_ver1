package org.example.kdt_bank_client2;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;


public class UnifiedApplication extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void start(Stage primaryStage) {
        // 초기 선택 화면
        showModeSelection(primaryStage);
    }

    private void showModeSelection(Stage stage) {
        VBox container = new VBox(30);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));

        Label titleLabel = new Label("시스템 선택");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button employeeButton = new Button("직원 시스템");
        employeeButton.setPrefSize(200, 50);
        employeeButton.setOnAction(e -> openEmployeeSystem(stage));

        Button customerButton = new Button("고객 채팅 시스템");
        customerButton.setPrefSize(200, 50);
        customerButton.setOnAction(e -> openCustomerSystem(stage));

        container.getChildren().addAll(titleLabel, employeeButton, customerButton);
        Scene scene = new Scene(container, 600, 400);
        stage.setScene(scene);
        stage.setTitle("통합 은행 시스템");
        stage.show();
    }

    private void openEmployeeSystem(Stage stage) {
        // 기존 직원 FXML 시스템 실행
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("M_signin_form.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 900);
            stage.setScene(scene);
            stage.setTitle("직원 시스템");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openCustomerSystem(Stage stage) {
        // Spring Boot 기반 채팅 클라이언트 실행
        initSpringContext();
        springContext.publishEvent(new ChatClientApp.StageReadyEvent(stage));
    }

    private void initSpringContext() {
        if (springContext == null) {
            System.setProperty("java.awt.headless", "false");
            springContext = new SpringApplicationBuilder(KdtBankServerProject2Application.class)
                    .headless(false)
                    .run();
        }
    }
}
