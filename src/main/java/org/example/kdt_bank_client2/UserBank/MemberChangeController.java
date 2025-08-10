package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerUpdateRequestDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MemberChangeController {

    @FXML private TextField  txtEmail, txtSSN, txtName, txtPhone, txtAddress;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnSearch, btnUpdate;

    private final CustomerService customerService;
    private final CustomerSession customerSession;

    @FXML
    public void initialize() {
        btnSearch.setOnAction(e -> onSearch());
        btnUpdate.setOnAction(e -> onUpdate());
    }
    // 이름 , 주민번호
    private void onSearch() {
        try {
            customerService.getCustomerByNameAndResidentNumber(txtName.getText(), txtSSN.getText());
            CustomerResponseDto dto = customerSession.getCustomerResponseDto();
            if (dto == null) { showAlert("찾기 실패","고객 없음"); return; }
            txtName.setText(dto.getName());
            txtPhone.setText(dto.getPhone());
            txtAddress.setText(dto.getAddress());
            txtSSN.setText(dto.getResidentNumber());
        } catch (Exception e) {
            showAlert("오류", e.getMessage());
        }
    }

    private void onUpdate() {
        try {
            String id =customerSession.getCustomer().getId();
            customerService.updateCustomer(id,new CustomerUpdateRequestDto(txtName.getText(),txtPhone.getText(), txtPassword.getText(), txtAddress.getText(), txtEmail.getText()));
            showAlert("성공", "수정 완료");
        } catch (Exception e) {
            showAlert("오류", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
