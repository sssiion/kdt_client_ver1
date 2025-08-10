package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberInquiryController {

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private TableView<CustomerResponseDto> tableCustomers;
    @FXML private TableColumn<CustomerResponseDto, String> colName, colResidentNumber, colPhone, colAddress;
    @FXML private Button btnViewDetails;

    private final CustomerService customerService;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colResidentNumber.setCellValueFactory(new PropertyValueFactory<>("residentNumber"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        btnSearch.setOnAction(e -> searchCustomer());
        btnViewDetails.setOnAction(e -> showDetails());
    }

    private void searchCustomer() {
        try {
            List<CustomerResponseDto> result = customerService.searchCustomers(txtSearch.getText());
            tableCustomers.setItems(FXCollections.observableArrayList(result));
        } catch (Exception e) {
            showAlert("검색 오류", e.getMessage());
        }
    }

    private void showDetails() {
        CustomerResponseDto selected = tableCustomers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("선택 필요", "고객을 선택하세요.");
            return;
        }
        String info = String.format("이름: %s\n주민번호: %s\n전화번호: %s\n이메일: %s\n주소: %s",
                selected.getName(), selected.getResidentNumber(), selected.getPhone(), selected.getEmail(), selected.getAddress());
        showAlert("고객 정보", info);
    }

    private void showAlert(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
}
