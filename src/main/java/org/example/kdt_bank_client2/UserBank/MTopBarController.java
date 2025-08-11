package org.example.kdt_bank_client2.UserBank;

import javafx.scene.control.Button;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.kdt_bank_client2.ChatClientApp;
import org.example.kdt_bank_client2.Session.UserSession;
import org.example.kdt_bank_client2.UnifiedApplication;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

import java.io.IOException;
@Controller
@RequiredArgsConstructor
@Setter
@Getter
public class MTopBarController {
    @FXML private HBox rootHBox;

    private M_MainController mainController;
    private final CustomerSession customerSession;
    private final UserSession userSession;
    public void setMainController(M_MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private Button btnAlarm;
    @FXML
    private void handleAlarmClick() {
        // 현재 Stage 가져오기
        Stage stage = (Stage) btnAlarm.getScene().getWindow();

        // 채팅 시스템 실행
        UnifiedApplication app = new UnifiedApplication();
        app.openCustomerSystem(stage);
    }

    @FXML
    private void NuriBankButton() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/kdt_bank_client2/M_home.fxml")
            );
            loader.setControllerFactory(UnifiedApplication.springContext::getBean);
            Parent homeScene = loader.load();
            mainController.showScreen(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        customerSession.setCustomerResponseDto(null);
        userSession.setCurrentUser(null);
        Stage stage = (Stage) rootHBox.getScene().getWindow();
        stage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kdt_bank_client2/M_signin_From.fxml"));
            loader.setControllerFactory(UnifiedApplication.springContext::getBean);
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("로그인");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}