package org.example.kdt_bank_client2.UserBank;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ControllerUser.LoanDocsController;
import org.example.kdt_bank_client2.DtoUser.LoanApplicationResponseDto;
import org.example.kdt_bank_client2.DtoUser.LoanDocsResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.LoanApplicationService;
import org.springframework.stereotype.Controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoanApprovalController {

    private final LoanApplicationService loanApplicationService;
    private final LoanDocsController loanDocsController;

    @FXML private TableView<LoanApplicationResponseDto> tableApplications;
    @FXML private TableColumn<LoanApplicationResponseDto, String> colAppId, colCustomerName, colProductName, colRequestedAmount, colStatus, colAppDate;
    @FXML private Button btnApprove, btnReject, btnRefresh;
    @FXML private ListView<String> docListView;

    @FXML
    public void initialize() {
        colAppId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApplicationId()));
        colCustomerName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));
        colProductName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductName()));
        colRequestedAmount.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%,.0f", d.getValue().getRequestedAmount())
        ));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colAppDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getApplicationDate().toString()
        ));

        btnRefresh.setOnAction(e -> loadPendingApplications());
        btnApprove.setOnAction(e -> approveSelected(true));
        btnReject.setOnAction(e -> approveSelected(false));

        tableApplications.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if (n != null) loadLoanDocuments(Long.valueOf(n.getApplicationId()));
        });

        loadPendingApplications();
    }

    private void loadPendingApplications() {
        try {
            List<LoanApplicationResponseDto> pending = loanApplicationService.getPendingApplications();
            tableApplications.setItems(FXCollections.observableArrayList(pending));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "대출 신청 목록 조회 실패").showAndWait();
        }
    }

    private void approveSelected(boolean approve) {
        LoanApplicationResponseDto selected = tableApplications.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "선택된 신청이 없습니다").showAndWait();
            return;
        }
        try {
            loanApplicationService.updateStatus(selected.getApplicationId(), approve ? "APPROVED" : "REJECTED");
            new Alert(Alert.AlertType.INFORMATION, "처리 완료").showAndWait();
            loadPendingApplications();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "처리 실패").showAndWait();
        }
    }

    private void loadLoanDocuments(Long applicationId) {
        try {
            List<LoanDocsResponseDto> docs = loanDocsController.listByApplicationId(applicationId);
            ObservableList<String> paths = FXCollections.observableArrayList();
            for (LoanDocsResponseDto doc : docs) {
                paths.add(doc.getFilePath());
            }
            docListView.setItems(paths);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "문서 목록 조회 실패").showAndWait();
        }
    }
}
