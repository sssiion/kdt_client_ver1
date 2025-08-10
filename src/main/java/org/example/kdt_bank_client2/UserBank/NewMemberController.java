package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.CustomerCreateRequestDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NewMemberController {

    @FXML private TextField txtName, txtEmail, txtResidentId, txtPhone, txtAddress;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnCancel, btnRegister;

    private final CustomerService customerService;

    @FXML
    public void initialize() {
        btnCancel.setOnAction(e -> clearFields());
        btnRegister.setOnAction(e -> handleRegister());
    }


    private void handleRegister() {
        try {
            customerService.createCustomer(new CustomerCreateRequestDto(
                    txtName.getText(), txtEmail.getText(), txtPassword.getText(),
                    txtPhone.getText(), txtResidentId.getText(), txtAddress.getText()
            ));
            showAlert("성공", "신규 고객 등록 완료");
            clearFields();
        } catch (Exception e) {
            showAlert("오류", e.getMessage());
        }
    }

    private void clearFields() {
        txtName.clear(); txtEmail.clear(); txtPassword.clear(); txtResidentId.clear();
        txtPhone.clear(); txtAddress.clear();
    }

    private void showAlert(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
}
