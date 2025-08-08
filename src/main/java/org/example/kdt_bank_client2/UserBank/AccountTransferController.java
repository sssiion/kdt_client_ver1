package org.example.kdt_bank_client2.UserBank;

import com.example.bank2.model.CustomerInfo;
import com.example.bank2.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class AccountTransferController {

    @FXML private ListView<Integer> lstAccounts;       // 고객 계좌 목록
    @FXML private TextField txtSelectedAccount;       // 출금(이체 출발) 계좌 번호
    @FXML private TextField txtTargetAccount;         // 입금(이체 도착) 계좌 번호
    @FXML private TextField txtAmount;                // 이체 금액
    @FXML private TextField txtNote;                  // 비고
    @FXML private Button btnTransfer;                 // 송금 실행
    @FXML private Button btnCancel;                   // 취소

    private ObservableList<Integer> accountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        CustomerInfo customer = Session.getCurrentCustomer();
        if (customer == null) {
            showAlert(Alert.AlertType.WARNING, "오류", "먼저 고객을 검색해주세요.");
            disableAll();
            return;
        }

        loadCustomerAccounts(customer.getId());

        lstAccounts.setItems(accountList);
        lstAccounts.setOnMouseClicked(this::onAccountSelected);

        btnTransfer.setOnAction(e -> onTransfer());
        btnCancel  .setOnAction(e -> onCancel());
    }

    private void disableAll() {
        lstAccounts.setDisable(true);
        btnTransfer.setDisable(true);
    }

    private void loadCustomerAccounts(int customerId) {
        accountList.clear();
        String sql = "SELECT account_number FROM account WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accountList.add(rs.getInt("account_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB 오류", "계좌 조회 중 오류가 발생했습니다.");
        }
    }

    private void onAccountSelected(MouseEvent ev) {
        Integer acc = lstAccounts.getSelectionModel().getSelectedItem();
        if (acc != null) {
            txtSelectedAccount.setText(String.valueOf(acc));
        }
    }

    private void onTransfer() {
        String srcAccStr = txtSelectedAccount.getText().trim();
        String dstAccStr = txtTargetAccount.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String note      = txtNote.getText().trim();

        if (srcAccStr.isEmpty() || dstAccStr.isEmpty() || amountStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "모든 필드를 입력하세요.");
            return;
        }

        int srcAcc, dstAcc;
        double amount;
        try {
            srcAcc = Integer.parseInt(srcAccStr);
            dstAcc = Integer.parseInt(dstAccStr);
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "입력 오류", "금액은 0보다 커야 합니다.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "유효한 숫자만 입력 가능합니다.");
            return;
        }

        String selectBalance = "SELECT amount  FROM account WHERE account_number = ?";
        String updateBalance = "UPDATE account SET amount  = ? WHERE account_number = ?";
        String insertTxn     = "INSERT INTO cash_transaction"
                + "(account_number, transaction_type, amount, note, balance_after_transaction)"
                + " VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1) 출금 계좌 잔액 조회
            double srcBalance;
            try (PreparedStatement ps = conn.prepareStatement(selectBalance)) {
                ps.setInt(1, srcAcc);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("출금 계좌가 없습니다.");
                    srcBalance = rs.getDouble(1);
                }
            }
            if (srcBalance < amount) {
                conn.rollback();
                showAlert(Alert.AlertType.ERROR, "잔액 부족", "출금 계좌의 잔액이 부족합니다.");
                return;
            }

            // 2) 입금 계좌 존재 확인 및 잔액 조회
            double dstBalance;
            try (PreparedStatement ps = conn.prepareStatement(selectBalance)) {
                ps.setInt(1, dstAcc);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("입금 계좌가 없습니다.");
                    dstBalance = rs.getDouble(1);
                }
            }

            // 3) 출금 처리
            double newSrcBalance = srcBalance - amount;
            try (PreparedStatement ps = conn.prepareStatement(updateBalance)) {
                ps.setDouble(1, newSrcBalance);
                ps.setInt(2, srcAcc);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(insertTxn)) {
                ps.setInt(1, srcAcc);
                ps.setString(2, "출금");
                ps.setDouble(3, amount);
                ps.setString(4, note.isEmpty() ? null : note);
                ps.setDouble(5, newSrcBalance);
                ps.executeUpdate();
            }

            // 4) 입금 처리
            double newDstBalance = dstBalance + amount;
            try (PreparedStatement ps = conn.prepareStatement(updateBalance)) {
                ps.setDouble(1, newDstBalance);
                ps.setInt(2, dstAcc);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(insertTxn)) {
                ps.setInt(1, dstAcc);
                ps.setString(2, "입금");
                ps.setDouble(3, amount);
                ps.setString(4, note.isEmpty() ? null : note);
                ps.setDouble(5, newDstBalance);
                ps.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "이체 완료",
                    String.format("출금 계좌: %d -> 잔액: %,.0f원\n입금 계좌: %d -> 잔액: %,.0f원",
                            srcAcc, newSrcBalance, dstAcc, newDstBalance));
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB 오류", "송금 처리 중 오류가 발생했습니다:\n" + e.getMessage());
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
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}