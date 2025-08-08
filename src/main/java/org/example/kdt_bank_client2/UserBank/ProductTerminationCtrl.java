package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class ProductTerminationCtrl {

    @FXML private Label lblCustomerName;
    @FXML private Label lblCustomerSSN;
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, Integer> colAccountNumber;
    @FXML private TableColumn<Account, String>  colProductName;
    @FXML private TableColumn<Account, String>  colStatus;
    @FXML private Button btnDeleteAccount;

    private ObservableList<Account> accountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 컬럼 매핑
        colAccountNumber.setCellValueFactory(cell -> cell.getValue().accountNumberProperty().asObject());
        colProductName .setCellValueFactory(cell -> cell.getValue().productNameProperty());
        colStatus      .setCellValueFactory(cell -> cell.getValue().statusProperty());

        // 현재 고객 정보 표시
        CustomerInfo customer = CustomerSession.getCurrentCustomer();
        if (customer == null) {
            showAlert(Alert.AlertType.WARNING, "오류", "먼저 고객을 검색해주세요.");
            return;
        }
        lblCustomerName.setText(customer.getName());
        lblCustomerSSN .setText(customer.getResidentNumber());

        // 계좌 조회
        loadCustomerAccounts(customer.getId());

        // 테이블에서 행 선택 시 버튼 활성화
        accountTable.setItems(accountList);
        accountTable.setOnMouseClicked(this::onRowClicked);
        btnDeleteAccount.setDisable(true);
    }

    private void loadCustomerAccounts(int customerId) {
        accountList.clear();
        String sql = "SELECT account_number, product_name, status FROM account WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accountList.add(new Account(
                            rs.getInt("account_number"),
                            rs.getString("product_name"),
                            0.0, // amount는 0으로 설정
                            null, // opening_date는 null로 설정
                            null, // closing_date는 null로 설정
                            rs.getString("status"),
                            "" // product_type은 빈 문자열로 설정
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB 오류", "계좌 조회 중 오류가 발생했습니다.");
        }
    }

    private void onRowClicked(MouseEvent event) {
        boolean selected = accountTable.getSelectionModel().getSelectedItem() != null;
        btnDeleteAccount.setDisable(!selected);
    }

    @FXML
    private void onDeleteAccount() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "선택 오류", "삭제할 계좌를 선택해주세요.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "계좌번호 " + selected.getAccountNumber() + "를 정말 삭제하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        String sql = "DELETE FROM account WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selected.getAccountNumber());
            int cnt = ps.executeUpdate();
            if (cnt == 1) {
                accountList.remove(selected);
                showAlert(Alert.AlertType.INFORMATION, "성공", "계좌가 삭제되었습니다.");
            } else {
                showAlert(Alert.AlertType.ERROR, "실패", "계좌 삭제에 실패했습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB 오류", "계좌 삭제 중 오류가 발생했습니다.");
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