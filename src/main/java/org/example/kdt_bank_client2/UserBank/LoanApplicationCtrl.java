package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.session.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanApplicationCtrl {

    @FXML private Label nameLabel, residentLabel, phoneLabel, emailLabel, addressLabel;

    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account,Integer> colAccNo;
    @FXML private TableColumn<Account,String>  colAccType;
    @FXML private TableColumn<Account,String>  colBalance;
    @FXML private TableColumn<Account,String>  colOpenDate;
    @FXML private TableColumn<Account,String>  colCloseDate;
    @FXML private TableColumn<Account,String>  colStatus;
    @FXML private TableColumn<Account,String>  colProdType;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product,String> colLoanName;
    @FXML private TableColumn<Product,String> colLoanDetail;
    @FXML private TableColumn<Product,String> colLoanLimit;

    @FXML private Button btnUpload, btnApply;

    private final ObservableList<File> attachedDocs = FXCollections.observableArrayList();
    private Product selectedProduct;

    @FXML
    public void initialize() {
        CustomerInfo cust = Session.getCurrentCustomer();
        if (cust == null) {
            new Alert(Alert.AlertType.WARNING, "고객을 먼저 검색하세요.").showAndWait();
            btnUpload.setDisable(true);
            btnApply .setDisable(true);
            return;
        }

        // 고객 정보
        nameLabel.setText(cust.getName());
        residentLabel.setText(cust.getResidentNumber());
        phoneLabel.setText(cust.getPhone());
        emailLabel.setText(cust.getEmail());
        addressLabel.setText(cust.getAddress());

        // 계좌 테이블 설정
        accountTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        colAccNo    .setCellValueFactory(data -> data.getValue().accountNumberProperty().asObject());
        colAccType  .setCellValueFactory(data -> data.getValue().productNameProperty());
        colBalance  .setCellValueFactory(data -> {
            double amount = data.getValue().getAmount();
            return new SimpleStringProperty(String.format("%,.0f", amount));
        });
        colOpenDate .setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOpeningDate()!=null
                                ? data.getValue().getOpeningDate().toString()
                                : ""
                )
        );
        colCloseDate.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getClosingDate()!=null
                                ? data.getValue().getClosingDate().toString()
                                : ""
                )
        );
        colStatus   .setCellValueFactory(data -> data.getValue().statusProperty());
        colProdType .setCellValueFactory(data -> data.getValue().productTypeProperty());
        loadAccounts(cust.getId());

        // 대출 상품 테이블 설정 (3개 컬럼)
        colLoanName.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        colLoanDetail.setCellValueFactory(new PropertyValueFactory<>("product_detail"));
        colLoanLimit.setCellValueFactory(data -> {
            try {
                double limit = Double.parseDouble(data.getValue().getLimit());
                return new SimpleStringProperty(String.format("%,.0f", limit));
            } catch (NumberFormatException e) {
                return new SimpleStringProperty(data.getValue().getLimit());
            }
        });
        loadProducts();

        // 상품 선택 리스너
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedProduct = n;
            btnUpload.setDisable(n == null);
            updateApplyButtonState();
        });

        btnUpload.setOnAction(e -> {
            onUpload();
            updateApplyButtonState();
        });
        btnApply.setOnAction(e -> onApply());
    }

    private void updateApplyButtonState() {
        // 상품 선택 + 첨부 문서 1개 이상 시에만 활성화
        btnApply.setDisable(selectedProduct == null || attachedDocs.isEmpty());
    }

    private void loadAccounts(int custId) {
        ObservableList<Account> list = FXCollections.observableArrayList();
        String sql = "SELECT account_number,product_name,amount,opening_date,closing_date,status,product_type "
                + "FROM account WHERE customer_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Account(
                            rs.getInt("account_number"),
                            rs.getString("product_name"),
                            rs.getDouble("amount"),
                            rs.getDate("opening_date").toLocalDate(),
                            rs.getDate("closing_date") != null
                                    ? rs.getDate("closing_date").toLocalDate()
                                    : null,
                            rs.getString("status"),
                            rs.getString("product_type")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "계좌 조회 중 오류 발생").showAndWait();
        }
        accountTable.setItems(list);
    }

    private void loadProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();
        // DB에 실제 존재하는 컬럼만 조회
        String sql = "SELECT product_name, product_detail, category, product_category,"
                + " max_rate, min_rate, limitmoney FROM product WHERE category='deachul'";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getString("product_name"),
                        rs.getString("product_detail"),
                        rs.getString("category"),
                        rs.getString("product_category"),
                        rs.getString("max_rate"),
                        rs.getString("min_rate"),
                        rs.getString("limitmoney")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "대출 상품 조회 중 오류 발생").showAndWait();
        }
        productTable.setItems(list);
    }


    private void onUpload() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF/Image","*.pdf","*.png","*.jpg"));
        List<File> sel = fc.showOpenMultipleDialog((Stage)btnUpload.getScene().getWindow());
        if (sel != null && !sel.isEmpty()) {
            attachedDocs.addAll(sel);
            new Alert(Alert.AlertType.INFORMATION, sel.size()+"개 서류 첨부됨").showAndWait();
        }
    }

    private void onApply() {
        if (selectedProduct == null || attachedDocs.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "상품을 선택하고 서류를 첨부해야 합니다.").showAndWait();
            return;
        }

        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            new Alert(Alert.AlertType.WARNING, "입금받을 계좌를 선택하세요.").showAndWait();
            return;
        }

        // 대출 금액 입력 받기
        TextInputDialog dialog = new TextInputDialog("10000000");
        dialog.setTitle("대출 신청");
        dialog.setHeaderText("대출 희망 금액을 입력하세요.");
        dialog.setContentText("금액:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // 사용자가 취소
        }

        double requestedAmount;
        try {
            requestedAmount = Double.parseDouble(result.get());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "유효한 금액을 입력하세요.").showAndWait();
            return;
        }

        String insertApplication = "INSERT INTO loan_application (customer_id, product_name, requested_amount, status, application_date, target_account_number) " +
                "VALUES (?, ?, ?, 'PENDING', NOW(), ?)";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(insertApplication, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, Session.getCurrentCustomer().getId());
                ps.setString(2, selectedProduct.getProduct_name());
                ps.setDouble(3, requestedAmount);
                ps.setInt(4, selectedAccount.getAccountNumber());
                ps.executeUpdate();

                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int applicationId = generatedKeys.getInt(1);
                    saveLoanDocuments(c, applicationId);
                }

                c.commit();

                new Alert(Alert.AlertType.INFORMATION, "대출 신청되었습니다.").showAndWait();
                attachedDocs.clear();
                updateApplyButtonState();
            } catch (SQLException | IOException ex) {
                c.rollback();
                throw ex;
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "대출 신청 중 오류 발생").showAndWait();
        }
    }

    private void saveLoanDocuments(Connection conn, int applicationId) throws SQLException, IOException {
        String insertDoc = "INSERT INTO loan_docs (application_id, file_path, file_name, file_type) VALUES (?, ?, ?, ?)";
        Path docDir = Paths.get("loan_documents");
        if (!Files.exists(docDir)) {
            Files.createDirectories(docDir);
        }

        for (File file : attachedDocs) {
            String fileName = applicationId + "_" + file.getName();
            Path targetPath = docDir.resolve(fileName);
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            try (PreparedStatement ps = conn.prepareStatement(insertDoc)) {
                ps.setInt(1, applicationId);
                ps.setString(2, targetPath.toString());
                ps.setString(3, file.getName());
                ps.setString(4, getFileExtension(file.getName()));
                ps.executeUpdate();
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자 없음
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
