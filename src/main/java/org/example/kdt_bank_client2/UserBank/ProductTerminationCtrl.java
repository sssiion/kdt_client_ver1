package org.example.kdt_bank_client2.UserBank;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Controller;


import java.util.List;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;


@Controller
@RequiredArgsConstructor
public class ProductTerminationCtrl {

    @FXML private Label lblCustomerName, lblCustomerSSN;
    @FXML private TableView<AccountResponseDto> accountTable;
    @FXML private Button btnDeleteAccount;

    private final CustomerSession customerSession;
    private final CustomerService customerService;

    @FXML
    public void initialize() {
        CustomerResponseDto cust = customerSession.getCustomerResponseDto();
        if (cust == null) return;
        lblCustomerName.setText(cust.getName());
        lblCustomerSSN.setText(cust.getResidentNumber());
        loadAccounts();

        btnDeleteAccount.setOnAction(e -> deleteSelected());
    }

    private void loadAccounts() {
        customerService.getAccountsByCustomerId(customerSession.getCustomerResponseDto().getId());
        List<AccountResponseDto> list = customerSession.getAccountResponseDtos();
        accountTable.setItems(FXCollections.observableArrayList(list));
    }

    private void deleteSelected() {
        AccountResponseDto sel = accountTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        // accountService.deleteAccount(sel.getAccountNumber()) 호출
        loadAccounts();
    }
}

