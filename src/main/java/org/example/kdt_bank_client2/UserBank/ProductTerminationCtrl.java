package org.example.kdt_bank_client2.UserBank;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductTerminationCtrl {

    @FXML private Label lblCustomerName, lblCustomerSSN;
    @FXML private TableView<AccountResponseDto> accountTable;
    @FXML private TableColumn<AccountResponseDto, String> colAccountNumber;
    @FXML private TableColumn<AccountResponseDto, String> amount;
    @FXML private TableColumn<AccountResponseDto, String> colProductName;
    @FXML private TableColumn<AccountResponseDto, String> colStatus;
    @FXML private Button btnDeleteAccount;

    private final CustomerSession customerSession;
    private final CustomerService customerService;

    @FXML
    public void initialize() {
        // 현재 고객 정보 표시
        CustomerResponseDto cust = customerSession.getCustomerResponseDto();
        if (cust == null) {
            showAlert(Alert.AlertType.WARNING, "오류", "먼저 고객을 검색해주세요.");
            btnDeleteAccount.setDisable(true);
            return;
        }
        lblCustomerName.setText(cust.getName());
        lblCustomerSSN.setText(cust.getResidentNumber());

        // 테이블 컬럼 매핑
        colAccountNumber.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAccountNumber()));
        colProductName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProductName()));
        amount.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAmount().toString()));

        // 계좌 목록 로드
        loadAccounts();

        // 행 선택 시 버튼 활성화
        accountTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> btnDeleteAccount.setDisable(newSel == null));

        // 삭제 버튼 이벤트
        btnDeleteAccount.setDisable(true); // 초기 상태 비활성화
        btnDeleteAccount.setOnAction(e -> deleteSelected());
    }

    private void loadAccounts() {
        accountTable.getItems().clear();
        customerService.getAccountsByCustomerId(customerSession.getCustomerResponseDto().getId());
        List<AccountResponseDto> list = customerSession.getAccountResponseDtos();
        accountTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void deleteSelected() {
        AccountResponseDto sel = accountTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "선택 오류", "삭제할 계좌를 선택하세요.");
            return;
        }

        // 삭제 확인
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("계좌 삭제 확인");
        confirm.setHeaderText(null);
        confirm.setContentText("계좌번호 " + sel.getAccountNumber() + " (" + sel.getProductName() + ") 를 삭제하시겠습니까?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // 삭제 처리
        try {
            customerService.deleteAccount(sel.getAccountNumber()); // API 호출
            showAlert(Alert.AlertType.INFORMATION, "성공", "계좌가 삭제되었습니다.");
            loadAccounts();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "오류", "계좌 삭제 실패: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
