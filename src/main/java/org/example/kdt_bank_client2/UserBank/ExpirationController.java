package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class ExpirationController {

    @FXML private Label lblName, lblResident, lblPhone, lblEmail, lblAddress;
    @FXML private TextField txtProductName;
    @FXML private Button btnSearch, btnRefresh;
    @FXML private TableView<ExpirationProduct> tableExpiration;
    @FXML private TableColumn<ExpirationProduct,Integer> colAccountNumber;
    @FXML private TableColumn<ExpirationProduct,String>  colProductName;
    @FXML private TableColumn<ExpirationProduct,String>  colOpeningDate;
    @FXML private TableColumn<ExpirationProduct,String>  colMaturityDate;
    @FXML private TableColumn<ExpirationProduct,Double>  colBalance;
    @FXML private TableColumn<ExpirationProduct,String>  colStatus;

    private final ObservableList<ExpirationProduct> expirationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        CustomerInfo cust = Session.getCurrentCustomer();
        if (cust == null) {
            new Alert(Alert.AlertType.WARNING, "먼저 고객을 검색하세요.").showAndWait();
            return;
        }
        // 고객 정보 표시
        lblName.setText(cust.getName());
        lblResident.setText(cust.getResidentNumber());
        lblPhone.setText(cust.getPhone());
        lblEmail.setText(cust.getEmail());
        lblAddress.setText(cust.getAddress());

        // 컬럼 매핑
        colAccountNumber.setCellValueFactory(c -> c.getValue().accountNumberProperty().asObject());
        colProductName   .setCellValueFactory(c -> c.getValue().productNameProperty());
        colOpeningDate   .setCellValueFactory(c -> c.getValue().openingDateProperty());
        colMaturityDate  .setCellValueFactory(c -> c.getValue().expirationDateProperty());
        colBalance       .setCellValueFactory(data -> data.getValue().balanceProperty().asObject());
        colBalance.setCellFactory(tc -> new TableCell<>() {
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
        colStatus        .setCellValueFactory(c -> c.getValue().statusProperty());

        // 버튼 이벤트
        btnSearch .setOnAction(e -> searchExpiration());
        btnRefresh.setOnAction(e -> refreshTable());

        loadExpirationData();
    }

    private void loadExpirationData() {
        expirationList.clear();
        String sql =
                "SELECT c.name AS customer_name, a.account_number, a.product_name, " +
                        "       a.opening_date, a.closing_date AS maturity_date, " +
                        "       a.amount AS balance, a.status " +
                        "FROM account a " +
                        "JOIN customer c ON a.customer_id = c.id " +
                        "WHERE a.customer_id = ? AND a.closing_date IS NOT NULL " +
                        "ORDER BY a.closing_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Session.getCurrentCustomer().getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expirationList.add(new ExpirationProduct(
                            rs.getString("customer_name"),       // 추가됨
                            rs.getInt("account_number"),
                            rs.getString("product_name"),
                            rs.getString("opening_date"),
                            rs.getString("maturity_date"),
                            rs.getDouble("balance"),
                            rs.getString("status")
                    ));
                }
            }
            tableExpiration.setItems(expirationList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchExpiration() {
        String prod = txtProductName.getText().trim();
        expirationList.clear();
        String sql =
                "SELECT c.name AS customer_name, a.account_number, a.product_name, " +
                        "       a.opening_date, a.closing_date AS maturity_date, " +
                        "       a.amount AS balance, a.status " +
                        "FROM account a " +
                        "JOIN customer c ON a.customer_id = c.id " +
                        "WHERE a.customer_id = ? " +
                        "  AND a.closing_date IS NOT NULL " +
                        "  AND a.product_name LIKE ? " +
                        "ORDER BY a.closing_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Session.getCurrentCustomer().getId());
            ps.setString(2, "%" + prod + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expirationList.add(new ExpirationProduct(
                            rs.getString("customer_name"),       // 추가됨
                            rs.getInt("account_number"),
                            rs.getString("product_name"),
                            rs.getString("opening_date"),
                            rs.getString("maturity_date"),
                            rs.getDouble("balance"),
                            rs.getString("status")
                    ));
                }
            }
            tableExpiration.setItems(expirationList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void refreshTable() {
        txtProductName.clear();
        loadExpirationData();
    }
}