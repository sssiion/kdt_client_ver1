package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.LoanAccountCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContractChangeCtrl {

    @FXML private Label lblName, lblPhone, lblResident, lblEmail, lblAddress;
    @FXML private VBox contractListVBox;
    @FXML private DatePicker newMaturityDatePicker;
    @FXML private CheckBox interestAdjustableCheckBox;
    @FXML private TextField newInterestRateField;
    @FXML private ComboBox<String> repaymentMethodComboBox;
    @FXML private TextField newLimitMoneyField;
    @FXML private Button saveChangeBtn;

    private final CustomerSession customerSession;
    private final CustomerService customerService;

    private LoanAccountResponseDto selectedLoanAccount;
    private final ObservableList<LoanAccountResponseDto> accountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        CustomerResponseDto dto = customerSession.getCustomerResponseDto();
        if (dto == null) {
            showAlert("경고", "먼저 고객을 검색하세요.");
            // UI 비활성화
            contractListVBox.setDisable(true); newMaturityDatePicker.setDisable(true);
            interestAdjustableCheckBox.setDisable(true); newInterestRateField.setDisable(true);
            repaymentMethodComboBox.setDisable(true); newLimitMoneyField.setDisable(true); saveChangeBtn.setDisable(true);
            return;
        }
        lblName.setText(dto.getName());
        lblPhone.setText(dto.getPhone());
        lblResident.setText(dto.getResidentNumber());
        lblEmail.setText(dto.getEmail());
        lblAddress.setText(dto.getAddress());
        repaymentMethodComboBox.getItems().addAll("만기일시상환", "원리금균등상환", "원금균등상환");
        disableChangeFields(true);
        loadAndShowContracts(dto.getId());
        saveChangeBtn.setOnAction(e -> onSaveChange());
    }

    private void disableChangeFields(boolean disable) {
        newMaturityDatePicker.setDisable(disable);
        interestAdjustableCheckBox.setDisable(disable);
        newInterestRateField.setDisable(disable);
        repaymentMethodComboBox.setDisable(disable);
        newLimitMoneyField.setDisable(disable);
        saveChangeBtn.setDisable(disable);
    }

    private void loadAndShowContracts(String customerId) {
        contractListVBox.getChildren().clear();
        customerService.getLoanAccountsByCustomerId(customerId);
        List<LoanAccountResponseDto> dtos = customerSession.getLoanAccountResponseDtos();
        for (LoanAccountResponseDto dto : dtos) {
            Button btn = new Button(
                    dto.getProductName() + " | 만기:" + dto.getMaturityDate() + " | 금리:" + dto.getInterestRate() + "%"
            );
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> onSelectContract(dto));
            contractListVBox.getChildren().add(btn);
        }
    }

    private void onSelectContract(LoanAccountResponseDto dto) {
        selectedLoanAccount = dto;
        newMaturityDatePicker.setValue(dto.getMaturityDate());
        newInterestRateField.setText(dto.getInterestRate() != null ? dto.getInterestRate().toString() : "");
        disableChangeFields(false);
    }
    @FXML
    private void onSaveChange() {
        if (selectedLoanAccount == null) {
            showAlert("오류", "변경할 계약을 선택하세요.");
            return;
        }
        LocalDate newDate = newMaturityDatePicker.getValue();
        if (newDate == null || newInterestRateField.getText().isEmpty()) {
            showAlert("오류", "새 만기일과 금리를 입력하세요.");
            return;
        }
        BigDecimal newRate;
        try {
            newRate = new BigDecimal(newInterestRateField.getText());
        } catch (NumberFormatException e) {
            showAlert("오류", "유효한 금리를 입력하세요.");
            return;
        }
        LoanAccountCreateRequestDto req = new LoanAccountCreateRequestDto();
        req.setCustomerId(selectedLoanAccount.getCustomerId());
        req.setProductName(selectedLoanAccount.getProductName());
        req.setInterestRate(newRate);
        req.setMaturityDate(newDate);
        // Optionally set limit, payType, etc.

        try {
            customerService.getLoanAccountChange(req);
            showAlert("성공", "대출 계약이 변경되었습니다.");
            loadAndShowContracts(customerSession.getCustomerResponseDto().getId());
            disableChangeFields(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("오류", "계약 변경 중 오류 발생: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null);
        a.setContentText(msg); a.showAndWait();
    }
}
