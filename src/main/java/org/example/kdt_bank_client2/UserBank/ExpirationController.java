package org.example.kdt_bank_client2.UserBank;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.LoanAccountService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExpirationController {

    @FXML private Label lblName, lblResident, lblPhone, lblEmail, lblAddress;
    @FXML private TextField txtProductName;
    @FXML private Button btnSearch, btnRefresh;
    @FXML private TableView<LoanAccountResponseDto> tableExpiration;
    @FXML private TableColumn<LoanAccountResponseDto, String> colAccountNumber;
    @FXML private TableColumn<LoanAccountResponseDto, String> colProductName;
    @FXML private TableColumn<LoanAccountResponseDto, String> colOpeningDate;
    @FXML private TableColumn<LoanAccountResponseDto, String> colMaturityDate;
    @FXML private TableColumn<LoanAccountResponseDto, Number> colBalance;
    @FXML private TableColumn<LoanAccountResponseDto, String> colStatus;

    private final ObservableList<LoanAccountResponseDto> expirationList = FXCollections.observableArrayList();
    private final CustomerSession customerSession;
    private final LoanAccountService loanAccountService;

    @FXML
    public void initialize() {
        CustomerResponseDto cust = customerSession.getCustomerResponseDto();
        if (cust == null) {
            new Alert(Alert.AlertType.WARNING, "먼저 고객을 검색하세요.").showAndWait();
            return;
        }

        // 고객 정보 표시
        lblName.setText(cust.getName());
        lblResident.setText(cust.getResidentNumber());
        lblPhone.setText(cust.getPhone());
        lblEmail.setText(cust.getEmail());
        lblAddress.setText(cust.getAddress());

        // 컬럼 매핑
        colAccountNumber.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLoanId()));
        colProductName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProductName()));
        colOpeningDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getLoanDate() != null ? data.getValue().getLoanDate().toString() : ""));
        colMaturityDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getMaturityDate() != null ? data.getValue().getMaturityDate().toString() : ""));
        colBalance.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalAmount()));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTotalAmount().subtract(data.getValue().getRepaymentAmount()).doubleValue() <= 0
                        ? "만기"
                        : "진행중"));

        colBalance.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", amount.doubleValue()));
                }
            }
        });

        // 버튼 이벤트
        btnSearch.setOnAction(e -> searchExpiration());
        btnRefresh.setOnAction(e -> refreshTable());

        // 최초 로드
        loadExpirationData();
    }

    /** 만기 임박 계좌 전체 조회 */
    private void loadExpirationData() {
        expirationList.clear();
        try {
            List<LoanAccountResponseDto> list =
                    loanAccountService.getLoansNearingMaturity(LocalDate.now()); // API에서 '만기 임박' 정의 날짜 기준
            expirationList.addAll(list);
            tableExpiration.setItems(expirationList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "데이터 로딩 실패: " + e.getMessage());
        }
    }

    /** 상품명으로 필터 검색 */
    private void searchExpiration() {
        String keyword = txtProductName.getText().trim();
        expirationList.clear();
        try {
            List<LoanAccountResponseDto> list;
            if (keyword.isEmpty()) {
                list = loanAccountService.getLoansNearingMaturity(LocalDate.now());
            } else {
                list = loanAccountService.getLoanAccountsByProductName(keyword);
            }
            expirationList.addAll(list);
            tableExpiration.setItems(expirationList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("검색 실패", e.getMessage());
        }
    }

    @FXML
    private void refreshTable() {
        txtProductName.clear();
        loadExpirationData();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
