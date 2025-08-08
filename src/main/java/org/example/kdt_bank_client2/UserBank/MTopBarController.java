package org.example.kdt_bank_client2.UserBank;

import com.example.bank2.session.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;

public class MTopBarController {
    @FXML private HBox rootHBox;

    private M_MainController mainController;

    public void setMainController(M_MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
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
        Session.setCurrentEmployee(null);
        Session.setCurrentCustomer(null);
        Stage stage = (Stage) rootHBox.getScene().getWindow();
        stage.close();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("M_signin_form.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("로그인");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}