package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class AgreementController {

    @FXML private TextField txtCustomerName;
    @FXML private ComboBox<String> comboAgreementStatus;
    @FXML private TableView<Agreement> tableAgreement;
    @FXML private TableColumn<Agreement, Integer> colAgreementId;
    @FXML private TableColumn<Agreement, String> colCustomerName;
    @FXML private TableColumn<Agreement, String> colProductName;
    @FXML private TableColumn<Agreement, String> colAgreementDate;
    @FXML private TableColumn<Agreement, String> colExpirationDate;
    @FXML private TableColumn<Agreement, String> colStatus;
    @FXML private TableColumn<Agreement, String> colNote;
    @FXML private Button btnSearch, btnRefresh, btnAdd, btnEdit, btnDelete;

    private final ObservableList<Agreement> agreementList = FXCollections.observableArrayList();
    private int currentCustomerId;

    @FXML
    public void initialize() {
        // 1) 세션에서 고객 정보 가져오기
        CustomerInfo cust = Session.getCurrentCustomer();
        if (cust == null) {
            new Alert(Alert.AlertType.WARNING, "먼저 고객을 검색하세요.").showAndWait();
            disableAll();
            return;
        }
        currentCustomerId = cust.getId();

        // 2) 컬럼 바인딩
        colAgreementId  .setCellValueFactory(c -> c.getValue().agreementIdProperty().asObject());
        colCustomerName .setCellValueFactory(c -> c.getValue().customerNameProperty());
        colProductName  .setCellValueFactory(c -> c.getValue().productNameProperty());
        colAgreementDate.setCellValueFactory(c -> c.getValue().agreementDateProperty());
        colExpirationDate.setCellValueFactory(c -> c.getValue().expirationDateProperty());
        colStatus       .setCellValueFactory(c -> c.getValue().statusProperty());
        colNote         .setCellValueFactory(c -> c.getValue().noteProperty());

        // 3) 상태 필터 설정
        comboAgreementStatus.getItems().addAll("전체", "ACTIVE", "INACTIVE", "EXPIRED");
        comboAgreementStatus.setValue("전체");

        // 4) 버튼 이벤트
        btnSearch .setOnAction(e -> searchAgreement());
        btnRefresh.setOnAction(e -> refreshTable());
        btnAdd    .setOnAction(e -> addAgreement());
        btnEdit   .setOnAction(e -> editAgreement());
        btnDelete .setOnAction(e -> deleteAgreement());

        // 5) 초기 데이터 로드 (고객별 약정)
        loadAgreementData();
    }

    private void disableAll() {
        txtCustomerName.setDisable(true);
        comboAgreementStatus.setDisable(true);
        btnSearch.setDisable(true);
        btnRefresh.setDisable(true);
        btnAdd.setDisable(true);
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        tableAgreement.setDisable(true);
    }

    private void loadAgreementData() {
        agreementList.clear();
        String sql =
                "SELECT a.agreement_id, c.name AS customer_name, a.product_name, " +
                        "       a.start_date AS agreement_date, a.maturity_date AS expiration_date, " +
                        "       a.status, a.terms AS note " +
                        "FROM agreement a " +
                        "JOIN customer c ON a.customer_id = c.id " +
                        "WHERE a.customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentCustomerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agreementList.add(new Agreement(
                            rs.getInt("agreement_id"),
                            rs.getString("customer_name"),
                            rs.getString("product_name"),
                            rs.getString("agreement_date"),
                            rs.getString("expiration_date"),
                            rs.getString("status"),
                            rs.getString("note")
                    ));
                }
            }
            tableAgreement.setItems(agreementList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchAgreement() {
        agreementList.clear();
        String nameFilter = txtCustomerName.getText().trim();
        String status = comboAgreementStatus.getValue();

        String sql =
                "SELECT a.agreement_id, c.name AS customer_name, a.product_name, " +
                        "       a.start_date AS agreement_date, a.maturity_date AS expiration_date, " +
                        "       a.status, a.terms AS note " +
                        "FROM agreement a " +
                        "JOIN customer c ON a.customer_id = c.id " +
                        "WHERE a.customer_id = ? AND c.name LIKE ?" +
                        (status.equals("전체") ? "" : " AND a.status = ?");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentCustomerId);
            ps.setString(2, "%" + nameFilter + "%");
            if (!status.equals("전체")) {
                ps.setString(3, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agreementList.add(new Agreement(
                            rs.getInt("agreement_id"),
                            rs.getString("customer_name"),
                            rs.getString("product_name"),
                            rs.getString("agreement_date"),
                            rs.getString("expiration_date"),
                            rs.getString("status"),
                            rs.getString("note")
                    ));
                }
            }
            tableAgreement.setItems(agreementList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshTable() {
        txtCustomerName.clear();
        comboAgreementStatus.setValue("전체");
        loadAgreementData();
    }

    @FXML
    private void addAgreement() {
        // TODO: 약정 추가 다이얼로그 오픈
    }

    @FXML
    private void editAgreement() {
        Agreement sel = tableAgreement.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "수정할 약정을 선택하세요.").showAndWait();
            return;
        }
        // TODO: sel 정보를 다이얼로그로 넘겨 수정 처리
    }

    @FXML
    private void deleteAgreement() {
        Agreement sel = tableAgreement.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "삭제할 약정을 선택하세요.").showAndWait();
            return;
        }
        String sql = "DELETE FROM agreement WHERE agreement_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sel.getAgreementId());
            ps.executeUpdate();
            loadAgreementData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
