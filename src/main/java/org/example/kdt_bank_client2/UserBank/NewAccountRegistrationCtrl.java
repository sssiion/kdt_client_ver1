package org.example.kdt_bank_client2.UserBank;

import com.example.bank2.session.Session;
import com.example.bank2.model.CustomerInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Priority;

public class NewAccountRegistrationCtrl {

    @FXML private Pane PaneContent;
    @FXML private VBox SelectMenu;
    @FXML private HBox CountTitle;

    @FXML private Label BlankLabel;

    @FXML private Text productcount;
    @FXML private VBox productValue;

    // 현재 선택된 카테고리
    private String selectedCategory = "전체";

    @FXML
    public void initialize() {
        refreshProductList();
    }

    // 버튼 클릭 시 카테고리 갱신 후 리스트 새로고침
    @FXML
    private void onCategorySelect(ActionEvent event) {
        Button btn = (Button) event.getSource();
        // userData 를 String 으로 꺼내기
        String categoryCode = (String) btn.getUserData();
        System.out.println("카테고리 코드: " + categoryCode);
        selectedCategory = categoryCode;
        refreshProductList();
    }


    // DB 조회 및 UI 갱신
    private void refreshProductList() {
        productValue.getChildren().clear();

        List<Product> products = getProductsFromDB(selectedCategory);
        productcount.setText(products.size() + "개의 상품이 검색되었습니다.");

        for (Product p : products) {
            HBox productBox = createProductItem(p);
            productValue.getChildren().add(productBox);
        }
    }

    // Product 하나를 표시할 HBox 생성
    private HBox createProductItem(Product product) {
        HBox hbox = new HBox(30);
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("subproduct");
        hbox.setMaxWidth(Double.MAX_VALUE);
        hbox.setMinHeight(110);
        hbox.setPrefHeight(120);
        hbox.setMaxHeight(130);

        // 체크박스 토글 버튼
        ToggleButton iconButton = new ToggleButton();
        ImageView icon = new ImageView(new Image(getClass()
                .getResourceAsStream("/com/example/bank2/icon/checkblack.png")));
        icon.setFitWidth(20); 
        icon.setFitHeight(20); 
        icon.setPreserveRatio(true);
        iconButton.setGraphic(icon);
        iconButton.setMinWidth(40);
        iconButton.setMinHeight(40);
        HBox.setHgrow(iconButton, Priority.NEVER);

        iconButton.setOnAction(e -> {
            boolean selected = iconButton.isSelected();
            String iconPath = selected
                    ? "/com/example/bank2/icon/checkcolor.png"
                    : "/com/example/bank2/icon/checkblack.png";
            ImageView iv = new ImageView(new Image(getClass()
                    .getResourceAsStream(iconPath)));
            iv.setFitWidth(20); 
            iv.setFitHeight(20); 
            iv.setPreserveRatio(true);
            iconButton.setGraphic(iv);
        });

        // 상품 정보
        VBox infoBox = new VBox(5);
        infoBox.getStyleClass().add("productValue");
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMinWidth(250);
        infoBox.setPrefWidth(350);
        infoBox.setMaxWidth(450);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Text name = new Text(product.getProduct_name());
        Text detail = new Text(product.getProduct_detail());
        name.getStyleClass().add("productname");
        detail.getStyleClass().add("productdetail");
        detail.setWrappingWidth(300); // 텍스트 줄바꿈
        infoBox.getChildren().addAll(name, detail);

        // 한도
        VBox limitBox = new VBox(3);
        limitBox.getStyleClass().add("limitbox");
        limitBox.setAlignment(Pos.CENTER);
        limitBox.setMinWidth(80);
        limitBox.setPrefWidth(100);
        limitBox.setMaxWidth(120);
        HBox.setHgrow(limitBox, Priority.NEVER);
        
        Text hando = new Text();
        try {
            double limit = Double.parseDouble(product.getLimit());
            hando.setText(String.format("%,.0f", limit));
        } catch (NumberFormatException e) {
            hando.setText(product.getLimit()); // 파싱 실패 시 원본 문자열 사용
        }
        hando.setFont(Font.font(16));
        limitBox.getChildren().addAll(new Text("한도"), hando);

        // 금리
        VBox rateBox = new VBox(3);
        rateBox.setAlignment(Pos.CENTER);
        rateBox.setMinWidth(100);
        rateBox.setPrefWidth(120);
        rateBox.setMaxWidth(150);
        HBox.setHgrow(rateBox, Priority.NEVER);
        
        TextFlow maxFlow = new TextFlow(
                new Text("최고 연 "), new Text(product.getMaxRate()), new Text("%")
        );
        TextFlow minFlow = new TextFlow(
                new Text("최저 연 "), new Text(product.getMinRate()), new Text("%")
        );
        rateBox.getChildren().addAll(maxFlow, minFlow);

        // 가입 버튼
        Button joinButton = new Button("상품 가입");
        joinButton.setMinWidth(80);
        joinButton.setPrefWidth(100);
        joinButton.setMaxWidth(120);
        joinButton.setMinHeight(50);
        joinButton.setPrefHeight(50);
        joinButton.setMaxHeight(60);
        joinButton.setStyle("-fx-font-size: 16;");
        HBox.setHgrow(joinButton, Priority.NEVER);
        
        joinButton.setOnAction(e -> {
            CustomerInfo customer = Session.getCurrentCustomer();
            if (customer == null) {
                showAlert("오류", "먼저 고객을 검색하고 로그인해주세요.");
                return;
            }
            String insertSql = "INSERT INTO account "
                    + "(product_name, customer_id, amount, opening_date, status, product_type) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {

                ps.setString(1, product.getProduct_name());
                ps.setInt   (2, customer.getId());
                ps.setBigDecimal(3, BigDecimal.ZERO);                       // 초기 잔액 0
                ps.setDate(4, Date.valueOf(LocalDate.now()));               // 오늘 날짜
                ps.setString(5, "ACTIVE");                                  // 계좌 상태
                ps.setString(6, product.getProduct_category());             // 상품 타입

                int affected = ps.executeUpdate();
                if (affected == 1) {
                    showAlert("성공", "계좌가 정상적으로 개설되었습니다.");
                    // 필요시 계좌 목록 갱신 메소드 호출
                } else {
                    showAlert("실패", "계좌 개설에 실패했습니다.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("오류", "계좌 개설 중 오류가 발생했습니다:\n" + ex.getMessage());
            }
        });

        hbox.getChildren().addAll(iconButton, infoBox, limitBox, rateBox, joinButton);
        return hbox;
    }



    // 선택된 카테고리로 DB 조회
    public List<Product> getProductsFromDB(String category) {
        List<Product> products = new ArrayList<>();
        String baseSql = "SELECT product_name, product_detail, category, product_category, "
                + "max_rate, min_rate, limitmoney FROM product ";
        String whereClause;
        boolean isAll = "전체".equals(category);
        if (isAll) {
            whereClause = "WHERE category NOT LIKE 'deachul'";
        } else {
            whereClause = "WHERE category = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(baseSql + whereClause)) {
            if (!isAll) {
                pstmt.setString(1, category);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                        rs.getString("product_name"),
                        rs.getString("product_detail"),
                        rs.getString("category"),
                        rs.getString("product_category"),
                        rs.getString("max_rate"),
                        rs.getString("min_rate"),
                        rs.getString("limitmoney")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}