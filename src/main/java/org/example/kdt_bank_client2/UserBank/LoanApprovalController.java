package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanApprovalController {
    @FXML private TableView<LoanApplication> tableApplications;
    @FXML private TableColumn<LoanApplication, Integer> colAppId;
    @FXML private TableColumn<LoanApplication, String> colCustomerName;
    @FXML private TableColumn<LoanApplication, String> colProductName;
    @FXML private TableColumn<LoanApplication, Double> colRequestedAmount;
    @FXML private TableColumn<LoanApplication, String> colStatus;
    @FXML private TableColumn<LoanApplication, String> colAppDate;
    @FXML private Button btnApprove, btnReject, btnRefresh;
    @FXML private ListView<String> docListView;

    @FXML
    public void initialize() {
        colAppId.setCellValueFactory(data -> data.getValue().applicationIdProperty().asObject());
        colCustomerName.setCellValueFactory(data -> data.getValue().customerNameProperty());
        colProductName.setCellValueFactory(data -> data.getValue().productNameProperty());
        colRequestedAmount.setCellValueFactory(data -> data.getValue().requestedAmountProperty().asObject());
        colRequestedAmount.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", amount));
                }
            }
        });
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        colAppDate.setCellValueFactory(data -> data.getValue().applicationDateProperty());
        loadPendingApplications();
        btnApprove.setOnAction(e -> approveSelected(true));
        btnReject.setOnAction(e -> approveSelected(false));
        btnRefresh.setOnAction(e -> loadPendingApplications());

        tableApplications.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadLoanDocuments(newSelection.getApplicationId());
            }
        });

        docListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedDoc = docListView.getSelectionModel().getSelectedItem();
                if (selectedDoc != null) {
                    openDocument(selectedDoc);
                }
            }
        });
    }

    private void loadPendingApplications() {
        ObservableList<LoanApplication> list = FXCollections.observableArrayList();
        String sql = "SELECT la.application_id, c.name, la.product_name, la.requested_amount, la.status, la.application_date " +
                "FROM loan_application la JOIN customer c ON la.customer_id = c.id WHERE la.status = 'PENDING'";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new LoanApplication(
                        rs.getInt("application_id"),
                        rs.getString("name"),
                        rs.getString("product_name"),
                        rs.getDouble("requested_amount"),
                        rs.getString("status"),
                        rs.getTimestamp("application_date").toString()
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "대출 신청 목록 조회 오류").showAndWait();
        }
        tableApplications.setItems(list);
    }

    private void approveSelected(boolean approve) {
        LoanApplication selected = tableApplications.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "신청을 선택하세요.").showAndWait();
            return;
        }
        String newStatus = approve ? "APPROVED" : "REJECTED";
        String sql = "UPDATE loan_application SET status = ? WHERE application_id = ?";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, selected.getApplicationId());
                ps.executeUpdate();

                if (approve) {
                    depositLoanAmount(c, selected.getApplicationId());
                }

                c.commit();
                new Alert(Alert.AlertType.INFORMATION, "처리되었습니다.").showAndWait();
                loadPendingApplications();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "처리 중 오류 발생").showAndWait();
        }
    }

    private void depositLoanAmount(Connection conn, int applicationId) throws SQLException {
        String query = "SELECT requested_amount, target_account_number FROM loan_application WHERE application_id = ?";
        double amount = 0;
        int accountNumber = 0;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    amount = rs.getDouble("requested_amount");
                    accountNumber = rs.getInt("target_account_number");
                }
            }
        }

        if (accountNumber > 0 && amount > 0) {
            String updateAccount = "UPDATE account SET amount = amount + ? WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateAccount)) {
                ps.setDouble(1, amount);
                ps.setInt(2, accountNumber);
                ps.executeUpdate();
            }

            String insertTransaction = "INSERT INTO cash_transaction (account_number, transaction_type, amount, balance_after_transaction, note) " +
                                       "VALUES (?, '입금', ?, (SELECT amount FROM account WHERE account_number = ?), '대출금 입금')";
            try (PreparedStatement ps = conn.prepareStatement(insertTransaction)) {
                ps.setInt(1, accountNumber);
                ps.setDouble(2, amount);
                ps.setInt(3, accountNumber);
                ps.executeUpdate();
            }
        }
    }

    private void loadLoanDocuments(int applicationId) {
        ObservableList<String> docList = FXCollections.observableArrayList();
        String sql = "SELECT file_path FROM loan_docs WHERE application_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    docList.add(rs.getString("file_path"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "첨부 서류 조회 중 오류 발생").showAndWait();
        }
        docListView.setItems(docList);
    }

    private void openDocument(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                new Alert(Alert.AlertType.ERROR, "파일을 찾을 수 없습니다: " + filePath).showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "파일을 여는 중 오류 발생").showAndWait();
        }
    }

    // LoanApplication DTO (내부 클래스)
    public static class LoanApplication {
        private final javafx.beans.property.IntegerProperty applicationId;
        private final javafx.beans.property.StringProperty customerName;
        private final javafx.beans.property.StringProperty productName;
        private final javafx.beans.property.DoubleProperty requestedAmount;
        private final javafx.beans.property.StringProperty status;
        private final javafx.beans.property.StringProperty applicationDate;

        public LoanApplication(int applicationId, String customerName, String productName, double requestedAmount, String status, String applicationDate) {
            this.applicationId = new javafx.beans.property.SimpleIntegerProperty(applicationId);
            this.customerName = new javafx.beans.property.SimpleStringProperty(customerName);
            this.productName = new javafx.beans.property.SimpleStringProperty(productName);
            this.requestedAmount = new javafx.beans.property.SimpleDoubleProperty(requestedAmount);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
            this.applicationDate = new javafx.beans.property.SimpleStringProperty(applicationDate);
        }
        public javafx.beans.property.IntegerProperty applicationIdProperty() { return applicationId; }
        public javafx.beans.property.StringProperty customerNameProperty() { return customerName; }
        public javafx.beans.property.StringProperty productNameProperty() { return productName; }
        public javafx.beans.property.DoubleProperty requestedAmountProperty() { return requestedAmount; }
        public javafx.beans.property.StringProperty statusProperty() { return status; }
        public javafx.beans.property.StringProperty applicationDateProperty() { return applicationDate; }
        public int getApplicationId() { return applicationId.get(); }
        public double getRequestedAmount() { return requestedAmount.get(); }
    }
}
