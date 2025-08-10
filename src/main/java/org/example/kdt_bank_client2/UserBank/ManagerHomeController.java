package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DTO.UserDataDto;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.example.kdt_bank_client2.Session.UserSession;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManagerHomeController {

    @FXML private ImageView employeeImage;
    @FXML private Label lblEmployeeName;
    @FXML private Label lblEmployeeDept;
    @FXML private Label lblScheduleCount;
    @FXML private Button btnNewAccount, btnCustomerInfo, btnLoanRequest, btnApproveLoan;
    @FXML private TextField txtCustomerName, txtCustomerSSN;
    @FXML private Button btnSearchCustomer;
    @FXML private HBox customerInfoSection;
    @FXML private Label lblInfoName, lblInfoSSN, lblInfoPhone, lblInfoEmail, lblInfoAddress;
    @FXML private VBox accountInfoSection;
    @FXML private TableView<AccountResponseDto> accountInfoTable;
    @FXML private TableColumn<AccountResponseDto, String> colAccountNumber, colAccountType, colProductType;
    @FXML private TableColumn<AccountResponseDto, Number> colBalance;
    @FXML private TableColumn<AccountResponseDto, String> colOpenDate, colClosingDate;

    private final CustomerService customerService;
    private final CustomerSession customerSession;
    private final UserSession userSession;

    @FXML
    public void initialize() {
        loadEmployeeInfo();
        setupTable();

        btnSearchCustomer.setOnAction(e -> searchCustomer());

        UserDataDto emp = userSession.getCurrentUserData();

        btnApproveLoan.setVisible(emp != null && "BT".equalsIgnoreCase(emp.getUserType().name()));
    }

    private void loadEmployeeInfo() {
        UserDataDto emp = userSession.getCurrentUserData();
        if (emp != null) {
            lblEmployeeName.setText(emp.getUserName());
            lblEmployeeDept.setText(emp.getUserType().name());
        }
    }

    private void setupTable() {
        colAccountNumber.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAccountNumber()));
        colAccountType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProductName()));
        colBalance.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount().doubleValue()));
        colProductType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
    }

    private void searchCustomer() {
        try {
            customerService.getCustomerByNameAndResidentNumber(txtCustomerName.getText(), txtCustomerSSN.getText());
            displayCustomer(customerSession.getCustomerResponseDto());
        } catch (Exception e) {
            showAlert("조회 실패", e.getMessage());
        }
    }

    private void displayCustomer(CustomerResponseDto cust) {
        if (cust == null) return;
        lblInfoName.setText(cust.getName());
        lblInfoSSN.setText(cust.getResidentNumber());
        lblInfoPhone.setText(cust.getPhone());
        lblInfoEmail.setText(cust.getEmail());
        lblInfoAddress.setText(cust.getAddress());
        customerInfoSection.setVisible(true);

        customerService.getAccountsByCustomerId(cust.getId());
        List<AccountResponseDto> list = customerSession.getAccountResponseDtos();
        accountInfoTable.setItems(FXCollections.observableArrayList(list));
        accountInfoSection.setVisible(!list.isEmpty());
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
