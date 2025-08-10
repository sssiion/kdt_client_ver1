package org.example.kdt_bank_client2.UserBank;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ControllerUser.LoanDocsController;
import org.example.kdt_bank_client2.DtoUser.*;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;

import org.example.kdt_bank_client2.UserBank.ServiceUser.LoanApplicationService;
import org.example.kdt_bank_client2.UserBank.ServiceUser.ProductService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.kdt_bank_client2.UserBank.SessionUser.ProductSession;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.List;
import java.util.Optional;
@Controller
@RequiredArgsConstructor
public class LoanApplicationCtrl {

    @FXML private Label nameLabel, residentLabel, phoneLabel, emailLabel, addressLabel;
    @FXML private TableView<LoanAccountResponseDto> accountTable;
    @FXML private TableColumn<LoanAccountResponseDto,String> colAccNo, colAccType, colBalance, colOpenDate, colCloseDate;
    @FXML private TableView<ProductResponseDto> productTable;
    @FXML private TableColumn<ProductResponseDto,String> colLoanName, colLoanDetail, colLoanLimit;
    @FXML private Button btnUpload, btnApply;

    private final CustomerSession customerSession;
    private final CustomerService customerService;
    private final ProductSession productSession;
    private final ProductService productService;
    private final LoanApplicationService loanApplicationService; // 신규 서비스
    private final LoanDocsController loanDocsController;         // 파일 업로드

    private final ObservableList<File> attachedDocs = FXCollections.observableArrayList();
    private ProductResponseDto selectedProduct;

    @FXML
    public void initialize() {
        CustomerResponseDto dto = customerSession.getCustomerResponseDto();
        if (dto == null) {
            new Alert(Alert.AlertType.WARNING, "고객을 먼저 검색하세요.").showAndWait();
            btnUpload.setDisable(true);
            btnApply .setDisable(true);
            return;
        }

        nameLabel.setText(dto.getName());
        residentLabel.setText(dto.getResidentNumber());
        phoneLabel.setText(dto.getPhone());
        emailLabel.setText(dto.getEmail());
        addressLabel.setText(dto.getAddress());

        colAccNo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLoanId()));
        colAccType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductName()));
        colBalance.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%,.0f", d.getValue().getTotalAmount().min(d.getValue().getRepaymentAmount()))
        ));
        colOpenDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() != null ? d.getValue().getCreatedAt().toLocalDate().toString() : ""));
        colCloseDate.setCellValueFactory(d -> new SimpleStringProperty(""));

        loadAccounts(dto.getId());
        colLoanName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colLoanDetail.setCellValueFactory(new PropertyValueFactory<>("productDetail"));
        colLoanLimit.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%,.0f", data.getValue().getLimitMoney())
        ));
        loadProducts();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedProduct = n;
            btnUpload.setDisable(n == null);
            updateApplyButtonState();
        });

        btnUpload.setOnAction(e -> { onUpload(); updateApplyButtonState(); });
        btnApply.setOnAction(e -> onApply());
    }

    private void updateApplyButtonState() {
        btnApply.setDisable(selectedProduct == null || attachedDocs.isEmpty());
    }

    private void loadAccounts(String custId) {
        customerService.getLoanAccountsByCustomerId(custId);
        accountTable.setItems(FXCollections.observableArrayList(customerSession.getLoanAccountResponseDtos()));
    }

    private void loadProducts() {
        productService.getAllProducts();
        productTable.setItems(FXCollections.observableArrayList(productSession.getProductResponseDtos()));
    }

    private void onUpload() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF/Image","*.pdf","*.png","*.jpg"));
        List<File> sel = fc.showOpenMultipleDialog(btnUpload.getScene().getWindow());
        if (sel != null) {
            attachedDocs.addAll(sel);
            new Alert(Alert.AlertType.INFORMATION, sel.size() + "개 서류 첨부됨").showAndWait();
        }
    }

    private void onApply() {
        LoanAccountResponseDto selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null || attachedDocs.isEmpty() || selectedAccount == null) {
            new Alert(Alert.AlertType.WARNING, "상품, 서류, 계좌를 모두 선택하세요.").showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog("10000000");
        dialog.setTitle("대출 신청");
        dialog.setHeaderText("대출 희망 금액을 입력하세요.");
        dialog.setContentText("금액:");
        Optional<String> amountOpt = dialog.showAndWait();
        if (amountOpt.isEmpty()) return;

        try {
            BigDecimal requestedAmount = new BigDecimal(amountOpt.get());
            LoanApplicationResponseDto appResp = loanApplicationService.createLoanApplication(
                    new LoanApplicationCreateRequestDto(
                            customerSession.getCustomerResponseDto().getId(),
                            selectedProduct.getProductName(),
                            requestedAmount,
                            selectedAccount.getLoanId() // or accountNumber
                    )
            );

            for (File file : attachedDocs) {
                loanDocsController.upload(
                        Long.valueOf(appResp.getApplicationId()),
                        file.toPath(),
                        getFileExtension(file.getName())
                );
            }

            new Alert(Alert.AlertType.INFORMATION, "대출 신청이 완료되었습니다.").showAndWait();
            attachedDocs.clear();
            updateApplyButtonState();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "대출 신청 중 오류 발생").showAndWait();
        }
    }

    private String getFileExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1);
    }
}
