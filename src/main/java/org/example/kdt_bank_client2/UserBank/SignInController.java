// SignInController.java
package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.EmployeeInfo;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignInController {

    @FXML private TextField idField;         // 이메일 입력
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = idField.getText().trim();
        String pw    = passwordField.getText().trim();
        if (email.isEmpty() || pw.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "이메일과 비밀번호를 모두 입력하세요.").showAndWait();
            return;
        }

        String sql = "SELECT employee_id, name, department, role FROM bank_employee WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pw);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 로그인 성공: 세션에 직원 정보 저장
                    EmployeeInfo emp = new EmployeeInfo(
                            rs.getInt("employee_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            email,
                            rs.getString("role")
                    );
                    CustomerSession.setCurrentEmployee(emp);

                    // 메인 화면으로 전환
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bank2/m_main_view.fxml"));
                    Parent mainRoot = loader.load();
                    Stage stage = (Stage) idField.getScene().getWindow();
                    stage.setScene(new Scene(mainRoot));
                } else {
                    new Alert(Alert.AlertType.ERROR, "이메일 또는 비밀번호가 올바르지 않습니다.").showAndWait();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "로그인 중 오류가 발생했습니다.").showAndWait();
        }
    }
}