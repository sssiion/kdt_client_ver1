package org.example.kdt_bank_client2.UserBank;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.Service.UserService;
import org.example.kdt_bank_client2.UnifiedApplication;
import org.springframework.stereotype.Controller;

import java.io.IOException;



@RequiredArgsConstructor
public class SignInController {

    @FXML private TextField idField;         // 이메일 입력
    @FXML private PasswordField passwordField;

    private final UserService userService;

    @FXML
    private void handleLogin() {
        String email = idField.getText().trim();
        String pw    = passwordField.getText().trim();
        if (email.isEmpty() || pw.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "이메일과 비밀번호를 모두 입력하세요.").showAndWait();
            return;
        }
        userService.login(email, pw,
                user -> Platform.runLater(() -> {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bank2/m_main_view.fxml"));
                    loader.setControllerFactory(UnifiedApplication.springContext::getBean);
                    Parent mainRoot = null;
                    try {
                        mainRoot=loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage = (Stage) idField.getScene().getWindow();
                    stage.setScene(new Scene(mainRoot));
                }),
                error -> Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "이메일 또는 비밀번호가 올바르지 않습니다.").showAndWait();
                })
        );


    }
}