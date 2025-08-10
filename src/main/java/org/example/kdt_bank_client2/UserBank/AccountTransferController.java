package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;

import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Controller;

import javafx.scene.input.MouseEvent;

import org.example.kdt_bank_client2.DtoUser.TransferRequestDto;


import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccountTransferController {

    @FXML private ListView<AccountResponseDto> lstAccounts; // 고객 계좌 목록
    @FXML private TextField txtSelectedAccount;             // 출금 계좌번호
    @FXML private TextField txtTargetAccount;               // 입금 계좌번호
    @FXML private TextField txtAmount;                      // 이체 금액
    @FXML private TextField txtNote;                        // 비고
    @FXML private Button btnTransfer;                       // 송금 실행
    @FXML private Button btnCancel;                         // 취소

    private final CustomerSession customerSession;
    private final CustomerService customerService;
    private final ObservableList<AccountResponseDto> accountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        CustomerResponseDto dto = customerSession.getCustomerResponseDto();
        if (dto == null) {
            showAlert(Alert.AlertType.WARNING, "오류", "먼저 고객을 검색해주세요.");
            disableAll();
            return;
        }
        loadCustomerAccounts(dto.getId());
        lstAccounts.setItems(accountList);
        lstAccounts.setOnMouseClicked(this::onAccountSelected);

        btnTransfer.setOnAction(e -> onTransfer());
        btnCancel.setOnAction(e -> onCancel());
    }

    private void disableAll() {
        lstAccounts.setDisable(true);
        btnTransfer.setDisable(true);
    }

    private void loadCustomerAccounts(String customerId) {
        accountList.clear();
        customerService.getAccountsByCustomerId(customerId); // 세션에 저장됨
        List<AccountResponseDto> dtos = customerSession.getAccountResponseDtos();
        accountList.addAll(dtos);
    }

    private void onAccountSelected(MouseEvent ev) {
        AccountResponseDto acc = lstAccounts.getSelectionModel().getSelectedItem();
        if (acc != null) {
            txtSelectedAccount.setText(acc.getAccountNumber());
        }
    }

    private void onTransfer() {
        String srcAccStr = txtSelectedAccount.getText().trim();
        String dstAccStr = txtTargetAccount.getText().trim();
        String amountStr = txtAmount.getText().trim();
        if (srcAccStr.isEmpty() || dstAccStr.isEmpty() || amountStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "모든 필드를 입력하세요.");
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.WARNING, "입력 오류", "금액은 0보다 커야 합니다.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "유효한 숫자만 입력 가능합니다.");
            return;
        }
        TransferRequestDto remittanceDto = new TransferRequestDto();
        remittanceDto.setAmount(amount);
        remittanceDto.setFromAccountNumber(srcAccStr);
        remittanceDto.setToAccountNumber(dstAccStr);
        // NOTE: 비고는 필요시 req.setNote(note) 처럼 구현 가능

        try {
            customerService.remittance(remittanceDto);
            showAlert(Alert.AlertType.INFORMATION, "송금 완료", "송금이 성공적으로 처리되었습니다.");
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "송금 실패", e.getMessage());
        }
    }

    private void onCancel() {
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "취소", "송금이 취소되었습니다.");
    }

    private void clearFields() {
        txtSelectedAccount.clear();
        txtTargetAccount.clear();
        txtAmount.clear();
        txtNote.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(msg); alert.showAndWait();
    }
}
