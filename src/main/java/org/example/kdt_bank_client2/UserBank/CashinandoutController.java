package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class CashinandoutController {

    @FXML private ListView<Integer> lstAccounts;
    @FXML private TextField txtSelectedAccount;
    @FXML private TextField txtAmount;
    @FXML private Button btnDeposit;
    @FXML private Button btnWithdraw;

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

        btnDeposit .setOnAction(e -> handleTransaction("입금"));
        btnWithdraw.setOnAction(e -> handleTransaction("출금"));
    }

    private void disableAll() {
        lstAccounts.setDisable(true);
        btnDeposit.setDisable(true);
        btnWithdraw.setDisable(true);
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

    private void handleTransaction(String type) {
        String accStr = txtSelectedAccount.getText().trim();
        String amtStr = txtAmount.getText().trim();
        if (accStr.isEmpty() || amtStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "계좌와 금액을 모두 입력하세요.");
            return;
        }
        int account;
        double amount;
        try {
            account = Integer.parseInt(accStr);
            amount  = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "유효한 금액을 입력하세요.");
            return;
        }

        String selectSql = "SELECT amount FROM account WHERE account_number=?";
        String updateSql = "UPDATE account SET amount=? WHERE account_number=?";
        String insertTxn = "INSERT INTO cash_transaction"
                + "(account_number, transaction_type, amount, note, balance_after_transaction)"
                + " VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 현재 잔액 조회
            double current;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, account);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("계좌가 없습니다.");
                    current = rs.getDouble(1);
                }
            }

            double newBalance = type.equals("입금")
                    ? current + amount
                    : current - amount;
            if (type.equals("출금") && newBalance < 0) {
                conn.rollback();
                showAlert(Alert.AlertType.ERROR, "잔액 부족", "출금할 잔액이 부족합니다.");
                return;
            }

            // 잔액 업데이트
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, newBalance);
                ps.setInt(2, account);
                ps.executeUpdate();
            }
            // 거래 기록
            try (PreparedStatement ps = conn.prepareStatement(insertTxn)) {
                ps.setInt(1, account);
                ps.setString(2, type);
                ps.setDouble(3, amount);
                ps.setString(4, null);
                ps.setDouble(5, newBalance);
                ps.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "거래 완료",
                    String.format("%s %,.0f원\n새 잔액: %,.0f원", type, amount, newBalance));
            txtAmount.clear();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB 오류", "거래 처리 중 오류가 발생했습니다:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}