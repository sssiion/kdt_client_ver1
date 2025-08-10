package org.example.kdt_bank_client2.UserBank;


import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.LoanAccountService;
import org.springframework.stereotype.Controller;
import java.math.BigDecimal;


@Controller
@RequiredArgsConstructor
public class RepaymentManagementCtrl {

    @FXML private Label lblName, lblPhone, lblResident, lblEmail, lblAddress;
    @FXML private VBox loanAccountsVBox;

    private final CustomerSession customerSession;
    private final LoanAccountService loanAccountService;

    @FXML
    public void initialize() {
        CustomerResponseDto cust = customerSession.getCustomerResponseDto();
        if (cust == null) {
            new Alert(Alert.AlertType.WARNING, "먼저 고객을 검색하세요.").showAndWait();
            loanAccountsVBox.setDisable(true);
            return;
        }

        lblName.setText(cust.getName());
        lblPhone.setText(cust.getPhone());
        lblResident.setText(cust.getResidentNumber());
        lblEmail.setText(cust.getEmail());
        lblAddress.setText(cust.getAddress());

        loadAndShowLoanAccounts(cust.getId());
    }

    private void loadAndShowLoanAccounts(String customerId) {
        loanAccountsVBox.getChildren().clear();
        try {
            List<LoanAccountResponseDto> accounts = loanAccountService.getLoanAccountsByCustomerId(customerId);
            for (LoanAccountResponseDto acc : accounts) {
                loanAccountsVBox.getChildren().add(createLoanAccountItem(acc));
            }
        } catch (Exception e) {
            showAlert("오류", "대출 계좌 조회 실패: " + e.getMessage());
        }
    }

    private TitledPane createLoanAccountItem(LoanAccountResponseDto acc) {
        VBox box = new VBox(6);
        Label lblId = new Label("대출ID: " + acc.getLoanId());
        Label lblTotal = new Label("대출금액: " + acc.getTotalAmount() + "원");
        TextField tfRep = new TextField(acc.getRepaymentAmount() != null ? acc.getRepaymentAmount().toString() : "0");

        Button btnSave = new Button("상환 저장");
        Label lblRemain = new Label();

        btnSave.setOnAction(e -> {
            try {
                LoanAccountResponseDto updated = loanAccountService.makeRepayment(
                        acc.getLoanId(),
                        new BigDecimal(tfRep.getText())
                );
                lblRemain.setText("잔액: " +
                        updated.getTotalAmount().subtract(updated.getRepaymentAmount()) + "원");
                showAlert("완료", "상환 처리 성공");
            } catch (Exception ex) {
                showAlert("에러", ex.getMessage());
            }
        });

        box.getChildren().addAll(lblId, lblTotal, new Label("상환액 입력:"), tfRep, btnSave, lblRemain);
        return new TitledPane(acc.getLoanId(), box);
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}

