package org.example.kdt_bank_client2.UserBank;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DtoUser.AccountCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.CustomerResponseDto;
import org.example.kdt_bank_client2.DtoUser.ProductResponseDto;
import org.example.kdt_bank_client2.UserBank.ServiceUser.CustomerService;
import org.example.kdt_bank_client2.UserBank.ServiceUser.ProductService;
import org.example.kdt_bank_client2.UserBank.SessionUser.CustomerSession;
import org.example.kdt_bank_client2.UserBank.SessionUser.ProductSession;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewAccountRegistrationCtrl {

    private final CustomerService customerService;
    @FXML private VBox productValue;
    @FXML private Text productcount;

    private String selectedCategory = "전체";

    private final ProductService productService;
    private final ProductSession productSession;
    private final CustomerSession customerSession;

    @FXML
    public void initialize() {
        refreshProductList();
    }

    /** 카테고리 버튼 클릭 시 호출 */
    @FXML
    private void onCategorySelect(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String categoryCode = (String) btn.getUserData();
        selectedCategory = categoryCode;
        refreshProductList();
    }

    /** 카테고리에 맞춰 상품 목록 로드 */
    private void refreshProductList() {
        productValue.getChildren().clear();

        // API 호출
        if ("전체".equals(selectedCategory)) {
            productService.getAllProducts();
        } else {
            productService.getProductsByCategory(selectedCategory);
        }

        List<ProductResponseDto> products = productSession.getProductResponseDtos();
        productcount.setText(products.size() + "개의 상품이 검색되었습니다.");

        for (ProductResponseDto p : products) {
            HBox productBox = createProductItem(p);
            productValue.getChildren().add(productBox);
        }
    }

    /** 상품 UI HBox 생성 */
    private HBox createProductItem(ProductResponseDto p) {
        HBox hbox = new HBox(30);
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("subproduct");

        // 상품명 + 설명
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMinWidth(250);
        Text name = new Text(p.getProductName());
        Text detail = new Text(p.getProductDetail());
        detail.setWrappingWidth(300);
        infoBox.getChildren().addAll(name, detail);

        // 한도
        VBox limitBox = new VBox(3);
        limitBox.setAlignment(Pos.CENTER);
        Text limitText = new Text(String.format("%,.0f", p.getLimitMoney()));
        limitText.setFont(Font.font(16));
        limitBox.getChildren().addAll(new Text("한도"), limitText);

        // 금리
        VBox rateBox = new VBox(3);
        rateBox.setAlignment(Pos.CENTER);
        TextFlow maxFlow = new TextFlow(new Text("최고 연 "), new Text(p.getMaxRate()+"%"));
        TextFlow minFlow = new TextFlow(new Text("최저 연 "), new Text(p.getMinRate()+"%"));
        rateBox.getChildren().addAll(maxFlow, minFlow);

        // 가입 버튼
        Button joinButton = new Button("상품 가입");
        joinButton.setOnAction(e -> {
            if (customerSession.getCustomerResponseDto() == null) {
                showAlert("오류", "먼저 고객을 검색하세요.");
                return;
            }
            // 여기서 accountService.createAccount(...) 호출
            CustomerResponseDto dto = customerSession.getCustomerResponseDto();
            customerService.createAccount(new AccountCreateRequestDto(dto.getId(),p.getProductName(),"적금"));
            showAlert("성공", p.getProductName() + " 가입 완료");
        });

        hbox.getChildren().addAll(infoBox, limitBox, rateBox, joinButton);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        return hbox;
    }

    private void showAlert(String t, String m) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(t);
        alert.setHeaderText(null);
        alert.setContentText(m);
        alert.showAndWait();
    }
}
