package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewMemberController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtResidentId;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtAddress;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnRegister;

    @FXML
    public void initialize() {
        // 취소 버튼 클릭 시 입력 필드 초기화
        btnCancel.setOnAction(e -> clearFields());

        // 등록 버튼 클릭 시 데이터 검증 후 처리
        btnRegister.setOnAction(e -> handleRegister());
    }

    // 등록 처리 로직
    private void handleRegister() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String residentId = txtResidentId.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        // 입력 값 검증
        if (name.isEmpty() || residentId.isEmpty() || phone.isEmpty()) {
            showAlert("입력 오류", "이름, 주민번호, 전화번호는 필수 입력 항목입니다.", Alert.AlertType.WARNING);
            return;
        }

        // 이메일 형식 검증
        if (!email.contains("@")) {
            showAlert("입력 오류", "올바른 이메일 주소를 입력해주세요.", Alert.AlertType.WARNING);
            return;
        }

        // 주민번호 간단 체크
        if (!residentId.matches("\\d{6}-\\d{7}")) {
            showAlert("입력 오류", "주민번호 형식은 000000-0000000 이어야 합니다.", Alert.AlertType.WARNING);
            return;
        }

        // DB 저장
        String sql = "INSERT INTO customer (name, email, password, phone, resident_number, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setString(5, residentId);
            pstmt.setString(6, address);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                showAlert("등록 성공", "신규 고객이 등록되었습니다.", Alert.AlertType.INFORMATION);
                clearFields();
            } else {
                showAlert("등록 실패", "고객 등록에 실패했습니다.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("DB 오류", "데이터베이스 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // 입력 필드 초기화
    private void clearFields() {
        txtName.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtResidentId.clear();
        txtPhone.clear();
        txtAddress.clear();
    }

    // 알림창 띄우기
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}