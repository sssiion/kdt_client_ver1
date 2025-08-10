package org.example.kdt_bank_client2.UserBank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.kdt_bank_client2.Session.UserSession;
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
    private void NuriBankButton() {
        try {
            Parent homeScene = FXMLLoader.load(getClass().getResource("M_home.fxml"));
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
            Parent root = FXMLLoader.load(getClass().getResource("M_signin_Form.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("로그인");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}