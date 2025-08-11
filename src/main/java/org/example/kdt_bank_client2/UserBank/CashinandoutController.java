package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CashTransactionResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.TransferRequestDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class CashinandoutController {

    @FXML private ListView<AccountResponseDto> lstAccounts;
    @FXML private TextField txtSelectedAccount, txtAmount;
    @FXML private Button btnDeposit, btnWithdraw;

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
        lstAccounts.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AccountResponseDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getAccountNumber()); // 계좌번호만 표시
                }
            }
        });

        btnDeposit.setOnAction(e -> handleTransaction("입금"));
        btnWithdraw.setOnAction(e -> handleTransaction("출금"));
    }

    private void disableAll() {
        lstAccounts.setDisable(true);
        btnDeposit.setDisable(true);
        btnWithdraw.setDisable(true);
    }

    private void loadCustomerAccounts(String customerId) {
        accountList.clear();
        customerService.getAccountsByCustomerId(customerId);
        List<AccountResponseDto> dtos = customerSession.getAccountResponseDtos();
        accountList.addAll(dtos);
    }

    private void onAccountSelected(MouseEvent ev) {
        AccountResponseDto acc = lstAccounts.getSelectionModel().getSelectedItem();
        if (acc != null) {
            txtSelectedAccount.setText(acc.getAccountNumber());
        }
    }

    private void handleTransaction(String type) {
        String accStr = txtSelectedAccount.getText().trim();
        String amtStr = txtAmount.getText().trim();
        if (accStr.isEmpty() || amtStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "계좌와 금액을 모두 입력하세요.");
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.WARNING, "입력 오류", "금액은 0보다 커야 합니다.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "유효한 숫자만 입력 가능합니다.");
            return;
        }

        TransferRequestDto dto = new TransferRequestDto();
        dto.setAmount(amtStr);
        CashTransactionResponseDto result;
        if (type.equals("입금")) {
            dto.setToAccountNumber(accStr);
            customerService.deposit(dto);
        } else if (type.equals("출금")) {
            dto.setFromAccountNumber(accStr);
           customerService.withdraw(dto);
        } else {
            showAlert(Alert.AlertType.WARNING, "타입 오류", "입금 또는 출금만 가능합니다.");
            return;
        }
        result = customerSession.getCashTransactionResponseDto();
        if (result != null) {
            showAlert(Alert.AlertType.INFORMATION, "거래 완료",
                    String.format("%s %,.0f원\n새 잔액: %,.0f원", type, amount, result.getAmount()));
            txtAmount.clear();
            loadCustomerAccounts(customerSession.getCustomerResponseDto().getId()); // 잔액 갱신 >> 입출금 금액
        } else {
            showAlert(Alert.AlertType.ERROR, "실패", "거래 실패");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(msg); alert.showAndWait();
    }
}
