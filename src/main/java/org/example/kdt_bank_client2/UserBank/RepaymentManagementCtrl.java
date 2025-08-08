package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepaymentManagementCtrl {

    @FXML private Label lblName, lblPhone, lblResident, lblEmail, lblAddress;
    @FXML private VBox loanAccountsVBox;

    private final List<LoanAccount> loanAccounts = new ArrayList<>();
    private LoanAccount selectedAccount;

    @FXML
    public void initialize() {
        CustomerInfo cust = CustomerSession.getCurrentCustomer();
        if (cust == null) {
            new Alert(Alert.AlertType.WARNING, "먼저 고객을 검색하세요.").showAndWait();
            loanAccountsVBox.setDisable(true);
            return;
        }


        lblName    .setText(cust.getName());
        lblPhone   .setText(cust.getPhone());
        lblResident.setText(cust.getResidentNumber());
        lblEmail   .setText(cust.getEmail());
        lblAddress .setText(cust.getAddress());

        // 2) 고객 대출 계좌 DB에서 로드 및 표시
        loadAndShowLoanAccounts(cust.getId());
    }

    private void loadAndShowLoanAccounts(int customerId) {
        if (CustomerSession.getCurrentCustomer() == null) {
            new Alert(Alert.AlertType.WARNING, "고객 정보가 없습니다.").showAndWait();
            return;
        }
        loanAccounts.clear();
        loanAccountsVBox.getChildren().clear();

        String sql = "SELECT loan_id, total_amount, repayment_amount FROM loan_account WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanAccount acc = new LoanAccount(
                            rs.getString("loan_id"),
                            rs.getLong("total_amount"),
                            rs.getLong("repayment_amount")
                    );
                    loanAccounts.add(acc);
                    loanAccountsVBox.getChildren().add(createLoanAccountItem(acc));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("DB 오류", "대출 계좌 조회 중 오류 발생");
        }
    }

    private TitledPane createLoanAccountItem(LoanAccount acc) {
        VBox box = new VBox(6);
        Label lblId    = new Label("계좌번호: " + acc.loanId);
        Label lblTotal = new Label("대출금액: " + String.format("%,d원", acc.totalLoan));
        TextField tfRep = new TextField(String.valueOf(acc.repayment));
        tfRep.setPrefWidth(120);
        Label lblRemain = new Label();

        tfRep.textProperty().addListener((o, oldV, newV) -> {
            try {
                acc.repayment = Long.parseLong(newV.replaceAll(",", ""));
            } catch (NumberFormatException ex) {
                tfRep.setText(oldV);
            }
        });

        Button btnSave = new Button("저장");
        btnSave.setOnAction(e -> {
            saveRepayment(acc);
            long remain = acc.totalLoan - acc.repayment;
            lblRemain.setText("상환 후 잔액: " + String.format("%,d원", remain));
        });

        box.getChildren().addAll(lblId, lblTotal, new Label("상환액:"), tfRep, btnSave, lblRemain);
        TitledPane pane = new TitledPane(acc.loanId, box);
        pane.setExpanded(false);
        return pane;
    }

    private void saveRepayment(LoanAccount acc) {
        String sql = "UPDATE loan_account SET repayment_amount = ? WHERE loan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, acc.repayment);
            ps.setString(2, acc.loanId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("DB 오류", "상환 정보 저장 중 오류 발생");
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static class LoanAccount {
        final String loanId;
        final long totalLoan;
        long repayment;
        LoanAccount(String loanId, long totalLoan, long repayment) {
            this.loanId = loanId;
            this.totalLoan = totalLoan;
            this.repayment = repayment;
        }
    }
}