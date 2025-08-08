package org.example.kdt_bank_client2;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.kdt_bank_client2.Controller.ChatRoomListController;
import org.example.kdt_bank_client2.Controller.UserController;
import org.example.kdt_bank_client2.Service.UserService;
import org.example.kdt_bank_client2.Service.ChatService;
import org.example.kdt_bank_client2.Session.UserSession;
import org.example.kdt_bank_client2.UI.FriendListController;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor                  // ChatService · AuthService만 생성자 주입
public class MainUIController {

    /* ---------- DI 대상 ---------- */
    private final ChatService chatService;
    private final UserService userService;
    private final UserSession userSession;        // 추가
    private final ApplicationContext applicationContext;
    private final UserController userController;


    /*==================================================================
     *  외부에서 DTO를 주입하는 메서드
     *==================================================================*/
    /* ---------- 런타임에 설정될 값 ---------- */
    @Setter
    private Stage           primaryStage;

    /* ---------- UI 컨테이너 ---------- */
    private BorderPane mainContainer;
    private StackPane  centerContainer;

    /* ---------- 화면 전환용 컨트롤러 ---------- */
    private FriendListController friendController;
    private ChatRoomListController chatController;


    /*==================================================================
     *  메인 Scene 생성
     *==================================================================*/
    public Scene createMainScene(Stage stage) {
        if (!userSession.isLoggedIn()) {
            throw new IllegalStateException("currentUser가 설정되지 않았습니다.");
        }
        setPrimaryStage(stage);

        mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: white;");

        createHeader();          // 상단 헤더
        createCenterContent();   // 중앙(친구·채팅 목록)
        createBottomMenu();      // 하단 메뉴

        return new Scene(mainContainer, 600, 1_000);
    }
    private void showChatplusDialog(){
        friendController.showUserListDialog();
    }
    /*==================================================================
     *  헤더
     *==================================================================*/
    private void createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

        Label titleLabel = new Label("친구");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: '맑은 고딕';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button plusButton = new Button("+");
        plusButton.setOnAction(e->{
            showChatplusDialog();
        });
        plusButton.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-border-color: black; -fx-border-width: 1;");
        Button logoutButton = new Button("logout");
        logoutButton.setOnAction(e -> {  // ✅ Button에 적절한 방식

            userController.logout();
            userSession.clearSession();
            StageInitializer stageInitializer = applicationContext.getBean(StageInitializer.class);
            stageInitializer.switchToLogin(primaryStage);
            if (friendController != null) {
                friendController.shutdown();
                System.out.println("✅ FriendListController 스케줄러 정지됨");
            }

        });

        header.getChildren().addAll(titleLabel, spacer,logoutButton, plusButton);
        mainContainer.setTop(header);
    }

    /*==================================================================
     *  중앙(친구 목록 / 채팅 목록)
     *==================================================================*/
    private void createCenterContent() {
        centerContainer = new StackPane();

        friendController = applicationContext.getBean(FriendListController.class);
        chatController = applicationContext.getBean(ChatRoomListController.class);

        centerContainer.getChildren().add(friendController.createFriendListView());  // 최초엔 친구 목록
        mainContainer.setCenter(centerContainer);
    }

    /*==================================================================
     *  하단 메뉴
     *==================================================================*/
    private void createBottomMenu() {
        HBox bottomMenu = new HBox();
        bottomMenu.setPrefHeight(100);
        bottomMenu.setStyle("-fx-background-color: pink;");

        Button friendBtn  = createMenuButton("👥", "친구");
        Button chatBtn    = createMenuButton("💬", "채팅");
        Button profileBtn = createMenuButton("👤", "프로필");

        friendBtn.setOnAction(e -> showFriendList());
        chatBtn.setOnAction(e   -> showChatList());
        profileBtn.setOnAction(e-> showProfile());

        bottomMenu.getChildren().addAll(friendBtn, chatBtn, profileBtn);
        mainContainer.setBottom(bottomMenu);
    }

    private Button createMenuButton(String icon, String tooltip) {
        Button btn = new Button(icon);
        btn.setPrefSize(200, 100);
        btn.setStyle("-fx-background-color: pink; -fx-font-size: 24px; -fx-border-width: 0;");

        // 간단한 호버·프레스 효과
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: pink; -fx-font-size: 24px; -fx-border-color: black; -fx-border-width: 1;"));
        btn.setOnMouseExited (e -> btn.setStyle("-fx-background-color: pink; -fx-font-size: 24px; -fx-border-width: 0;"));
        btn.setOnMousePressed(e -> btn.setStyle("-fx-background-color: white; -fx-font-size: 24px; -fx-border-color: black; -fx-border-width: 1;"));
        btn.setOnMouseReleased(e-> btn.setStyle("-fx-background-color: pink; -fx-font-size: 24px; -fx-border-width: 0;"));

        Tooltip.install(btn, new Tooltip(tooltip));
        return btn;
    }

    /*==================================================================
     *  메뉴 동작
     *==================================================================*/
    private void showFriendList() {
        centerContainer.getChildren().setAll(friendController.createFriendListView());
        setHeaderTitle("친구");
    }

    private void showChatList() {
        chatController.setPrimaryStage(primaryStage);
        centerContainer.getChildren().setAll(chatController.createChatListView());
        setHeaderTitle("채팅");
    }

    private void showProfile() {
        // 현재 사용자 정보 표시
        var currentUser = userSession.getCurrentUser();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("프로필");
        alert.setHeaderText("사용자 정보");
        alert.setContentText(
                "이름: " + currentUser.getUserName() + "\n" +
                        "ID: " + currentUser.getUserId() + "\n" +
                        "전화번호: " + (currentUser.getUserPhone() != null ? currentUser.getUserPhone() : "미등록") + "\n" +
                        "온라인 상태: " + (currentUser.getIsOnline() != null && currentUser.getIsOnline() ? "온라인" : "오프라인")
        );
        alert.showAndWait();
    }

    private void setHeaderTitle(String text) {
        Label title = (Label) ((HBox) mainContainer.getTop()).getChildren().get(0);
        title.setText(text);
    }
}
