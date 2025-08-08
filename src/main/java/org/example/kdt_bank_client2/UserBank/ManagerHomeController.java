package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import org.example.kdt_bank_client2.UserBank.model.EmployeeInfo;
import org.example.kdt_bank_client2.UserBank.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class ManagerHomeController {

    // 직원 정보 관련
    @FXML private ImageView employeeImage;
    @FXML private Label lblEmployeeName;
    @FXML private Label lblEmployeeDept;
    @FXML private Label lblScheduleCount;

    // 상단 빠른 메뉴 버튼
    @FXML private Button btnNewAccount;
    @FXML private Button btnCustomerInfo;
    @FXML private Button btnLoanRequest;
    @FXML private Button btnApproveLoan;

    // 고객 검색 입력
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtCustomerSSN;
    @FXML private Button btnSearchCustomer;

    // 고객 정보 표시용 레이블
    @FXML private HBox customerInfoSection;
    @FXML private Label lblInfoName;
    @FXML private Label lblInfoSSN;
    @FXML private Label lblInfoPhone;
    @FXML private Label lblInfoEmail;
    @FXML private Label lblInfoAddress;

    // 계좌 정보 테이블
    @FXML private VBox accountInfoSection;
    @FXML private TableView<Account> accountInfoTable;
    @FXML private TableColumn<Account, Integer>  colAccountNumber;
    @FXML private TableColumn<Account, String>   colAccountType;
    @FXML private TableColumn<Account, Double>   colBalance;
    @FXML private TableColumn<Account, LocalDate> colOpenDate;
    @FXML private TableColumn<Account, String>   colStatus;
    @FXML private TableColumn<Account, LocalDate> colClosingDate;
    @FXML private TableColumn<Account, String>    colProductType;

    private int currentCustomerId = -1;

    @FXML
    public void initialize() {
        loadEmployeeInfo();
        setupAccountTableColumns();

        btnCustomerInfo.setOnAction(e -> handleCustomerInfo());
        btnNewAccount.setOnAction(e -> handleNewAccount());
        btnLoanRequest.setOnAction(e -> handleLoanRequest());
        btnSearchCustomer.setOnAction(e -> searchCustomer());
        // ADMIN만 대출 승인 버튼 보이게
        EmployeeInfo emp = Session.getCurrentEmployee();
        if (emp != null && "ADMIN".equals(emp.getRole())) {
            btnApproveLoan.setVisible(true);
        } else {
            btnApproveLoan.setVisible(false);
        }
        btnApproveLoan.setOnAction(e -> handleApproveLoan());
        // 프로필(직원 이미지) 클릭 시 로그아웃 및 로그인 화면 이동
        employeeImage.setOnMouseClicked(e -> handleLogout());

        // 세션에 고객 정보가 남아 있으면 자동으로 표시
        CustomerInfo sessCust = Session.getCurrentCustomer();
        if (sessCust != null) {
            displayCustomer(sessCust);
        }
    }

    private void loadEmployeeInfo() {
        EmployeeInfo emp = Session.getCurrentEmployee();
        if (emp == null) {
            new Alert(Alert.AlertType.WARNING, "로그인 정보가 없습니다.").showAndWait();
            return;
        }
        String sql = "SELECT name, department FROM bank_employee WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emp.getEmployeeId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblEmployeeName.setText(rs.getString("name") + " 사원");
                    lblEmployeeDept.setText(rs.getString("department"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAccountTableColumns() {
        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colAccountType  .setCellValueFactory(new PropertyValueFactory<>("productName"));
        colBalance      .setCellValueFactory(data -> data.getValue().amountProperty().asObject());
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
        colOpenDate     .setCellValueFactory(new PropertyValueFactory<>("openingDate"));
        colClosingDate  .setCellValueFactory(new PropertyValueFactory<>("closingDate"));
        colStatus       .setCellValueFactory(new PropertyValueFactory<>("status"));
        colProductType  .setCellValueFactory(new PropertyValueFactory<>("productType"));
    }

    @FXML
    private void searchCustomer() {
        String name = txtCustomerName.getText().trim();
        String ssn  = txtCustomerSSN.getText().trim();
        if (name.isEmpty() || ssn.isEmpty()) {
            showAlert("입력 오류", "고객명과 주민등록번호를 모두 입력해주세요.");
            return;
        }
        String sql = "SELECT * FROM customer WHERE name = ? AND resident_number = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, ssn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CustomerInfo customer = new CustomerInfo(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("resident_number"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("address")
                    );
                    Session.setCurrentCustomer(customer);
                    displayCustomer(customer);
                } else {
                    showAlert("검색 결과", "해당 고객을 찾을 수 없습니다.");
                    customerInfoSection.setVisible(false);
                    accountInfoSection.setVisible(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("오류", "고객 검색 중 오류가 발생했습니다.");
        }
    }

    private void displayCustomer(CustomerInfo customer) {
        currentCustomerId = customer.getId();
        lblInfoName.setText(customer.getName());
        lblInfoSSN .setText(customer.getResidentNumber());
        lblInfoPhone.setText(customer.getPhone());
        lblInfoEmail.setText(customer.getEmail());
        lblInfoAddress.setText(customer.getAddress());
        customerInfoSection.setVisible(true);
        loadCustomerAccounts(currentCustomerId);
    }

    private void loadCustomerAccounts(int customerId) {
        String sql = "SELECT account_number, product_name, amount, opening_date, closing_date, status, product_type "
                + "FROM account WHERE customer_id = ?";
        ObservableList<Account> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Account(
                            rs.getInt("account_number"),
                            rs.getString("product_name"),
                            rs.getDouble("amount"),
                            rs.getDate("opening_date").toLocalDate(),
                            rs.getObject("closing_date") != null
                                    ? rs.getDate("closing_date").toLocalDate() : null,
                            rs.getString("status"),
                            rs.getString("product_type")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("오류", "계좌 정보 로딩 중 오류가 발생했습니다.");
        }
        accountInfoTable.setItems(list);
        accountInfoSection.setVisible(!list.isEmpty());
    }

    private void handleCustomerInfo() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("M_MemberChange.fxml"));
            Stage stage = new Stage();
            stage.setTitle("고객 정보 변경");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "고객 정보 변경 화면 로딩 실패");
        }
    }

    private void handleNewAccount() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("newAccountRegistration.fxml"));
            Stage stage = new Stage();
            stage.setTitle("신규 계좌 / 상품 가입");
            stage.setScene(new Scene(root));
            // 창 크기 제한 설정
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaxWidth(1200);
            stage.setMaxHeight(800);
            // 초기 크기 설정
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "신규 계좌 화면 로딩 실패");
        }
    }

    private void handleLoanRequest() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("loanApplication.fxml"));
            Stage stage = new Stage();
            stage.setTitle("대출 신청 및 심사");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "대출 신청 화면 로딩 실패");
        }
    }

    private void handleApproveLoan() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("LoanApproval.fxml"));
            Stage stage = new Stage();
            stage.setTitle("대출 승인 관리");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "대출 승인 화면 로딩 실패");
        }
    }

    private void handleLogout() {
        // 세션 초기화
        Session.setCurrentEmployee(null);
        Session.setCurrentCustomer(null);
        // 현재 창 닫기
        Stage stage = (Stage) employeeImage.getScene().getWindow();
        stage.close();
        // 로그인 화면 열기
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("로그인");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("오류", "로그인 화면 로딩 실패");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}