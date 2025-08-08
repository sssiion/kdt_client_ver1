package org.example.kdt_bank_client2.UserBank;

import org.example.kdt_bank_client2.UserBank.model.CustomerInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class MemberInquiryController {

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<CustomerInfo> tableCustomers;

    @FXML
    private TableColumn<CustomerInfo, String> colName;

    @FXML
    private TableColumn<CustomerInfo, String> colResidentNumber;

    @FXML
    private TableColumn<CustomerInfo, String> colPhone;

    @FXML
    private TableColumn<CustomerInfo, String> colAddress;

    @FXML
    private Button btnViewDetails;

    private ObservableList<CustomerInfo> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colResidentNumber.setCellValueFactory(new PropertyValueFactory<>("residentNumber"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        btnSearch.setOnAction(e -> searchCustomer());
        btnViewDetails.setOnAction(e -> showCustomerDetails());
    }

    // 고객 검색 기능
    private void searchCustomer() {
        String keyword = txtSearch.getText().trim();
        customerList.clear();

        String sql = "SELECT name, resident_number, phone, address FROM customer " +
                "WHERE name LIKE ? OR phone LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String residentNumber = rs.getString("resident_number");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                // 생년월일 변환 (앞 6자리 YYMMDD → YYYY-MM-DD)
                String birth = residentNumber != null && residentNumber.length() >= 6
                        ? formatBirth(residentNumber.substring(0, 6))
                        : "";

                customerList.add(new CustomerInfo(0, name, residentNumber, phone, "", address));
            }

            tableCustomers.setItems(customerList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "데이터베이스 조회 중 오류가 발생했습니다.");
        }
    }

    // 상세 정보 보기
    private void showCustomerDetails() {
        CustomerInfo selected = tableCustomers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("알림", "상세 정보를 볼 고객을 선택하세요.");
            return;
        }

        String details = "이름: " + selected.getName() + "\n" +
                "주민번호: " + selected.getResidentNumber() + "\n" +
                "전화번호: " + selected.getPhone() + "\n" +
                "이메일: " + selected.getEmail() + "\n" +
                "주소: " + selected.getAddress();

        showAlert("고객 상세 정보", details);
    }

    // 생년월일 포맷 변환
    private String formatBirth(String birth) {
        String yearPrefix = Integer.parseInt(birth.substring(0, 2)) <= 24 ? "20" : "19";
        return yearPrefix + birth.substring(0, 2) + "-" +
                birth.substring(2, 4) + "-" +
                birth.substring(4, 6);
    }

    // 알림창 표시
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}