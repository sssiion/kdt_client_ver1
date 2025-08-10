package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;





import javafx.scene.control.*;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.ProductResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.ProductService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.example.kdt_bank_client2.UserBank.SessionUser.ProductSession;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class NewAccountRegistrationCtrl {

    @FXML private VBox productValue;
    @FXML private Label productcount;

    private final ProductService productService;
    private final ProductSession productSession;
    private final CustomerSession customerSession;

    @FXML
    public void initialize() {
        loadProducts();
    }

    private void loadProducts() {
        productValue.getChildren().clear();
        productService.getAllProducts();
        for (ProductResponseDto p : productSession.getProductResponseDtos()) {
            Button b = new Button(p.getProductName() + " 가입");
            b.setOnAction(e -> joinProduct(p));
            productValue.getChildren().add(b);
        }
        productcount.setText(productSession.getProductResponseDtos().size() + "개 상품");
    }

    private void joinProduct(ProductResponseDto p) {
        if (customerSession.getCustomerResponseDto() == null) {
            showAlert("오류", "고객을 먼저 선택하세요.");
            return;
        }
        try {
            AccountCreateRequestDto req = new AccountCreateRequestDto();
            req.setProductName(p.getProductName());
            req.setCustomerId(customerSession.getCustomerResponseDto().getId());
            req.setAmount(BigDecimal.ZERO);
            // AccountService.createAccount(req) 호출
            showAlert("성공", "계좌 개설 완료");
        } catch (Exception e) {
            showAlert("실패", e.getMessage());
        }
    }

    private void showAlert(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
}
