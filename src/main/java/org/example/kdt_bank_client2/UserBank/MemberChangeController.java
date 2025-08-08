package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class MemberChangeController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtSSN;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtAddress;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    public void initialize() {
        btnSearch.setOnAction(event -> onSearch());
        btnUpdate.setOnAction(event -> onUpdate());
    }

    /** 고객 정보 조회 */
    private void onSearch() {
        String email = txtEmail.getText().trim();
        String ssn = txtSSN.getText().trim();

        if (email.isEmpty() && ssn.isEmpty()) {
            showAlert("조회 오류", "이메일 또는 주민번호를 입력하세요.");
            return;
        }

        String query = "SELECT * FROM customer WHERE email = ? OR ssn = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, ssn);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                txtEmail.setText(rs.getString("email"));
                txtPassword.setText(rs.getString("password"));
                txtSSN.setText(rs.getString("ssn"));
                txtName.setText(rs.getString("name"));
                txtPhone.setText(rs.getString("phone"));
                txtAddress.setText(rs.getString("address"));
                showAlert("조회 완료", "고객 정보를 불러왔습니다.");
            } else {
                showAlert("조회 실패", "해당 고객 정보를 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            showAlert("DB 오류", e.getMessage());
        }
    }

    /** 고객 정보 변경 */
    private void onUpdate() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String ssn = txtSSN.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (email.isEmpty() || password.isEmpty() || ssn.isEmpty() ||
                name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showAlert("입력 오류", "모든 정보를 입력해야 합니다.");
            return;
        }

        String query = "UPDATE customer SET password=?, name=?, phone=?, address=? WHERE email=? OR ssn=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, password);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setString(5, email);
            pstmt.setString(6, ssn);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                showAlert("변경 완료", "고객 정보가 성공적으로 수정되었습니다.");
            } else {
                showAlert("변경 실패", "고객 정보를 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            showAlert("DB 오류", e.getMessage());
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