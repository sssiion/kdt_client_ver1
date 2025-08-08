package org.example.kdt_bank_client2.UI;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.MainUIController;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.Service.UserService;
import org.example.kdt_bank_client2.Service.ChatService;
import org.example.kdt_bank_client2.Session.UserSession;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final ChatService chatService;
    private final UserSession userSession;           // 추가
    private final ApplicationContext applicationContext; // 추가

    private Stage primaryStage;

    @PostConstruct
    public void init() {
        System.out.println("🔍 LoginController 초기화됨");
        System.out.println("  - userService: " + (userService != null ? "정상" : "NULL"));
        System.out.println("  - userSession: " + (userSession != null ? "정상" : "NULL"));
        System.out.println("  - chatService: " + (chatService != null ? "정상" : "NULL"));
        System.out.println("  - applicationContext: " + (applicationContext != null ? "정상" : "NULL"));
    }
    public Scene createLoginScene(Stage stage) {
        this.primaryStage = stage;

        // 메인 컨테이너
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));
        mainContainer.setStyle("-fx-background-color: white;");

        // 타이틀 - 기존 Swing의 "Login" 제목 유지
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: black; -fx-font-family: '맑은 고딕';");

        // 로그인 폼 컨테이너
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(400);

        // 입력 필드들 - 기존 InputComponent 스타일 반영
        TextField userIdField = createStyledTextField("id");
        PasswordField passwordField = createStyledPasswordField("pw");

        // 버튼들 - 기존 스타일 반영
        Button loginButton = createStyledButton("로그인");
        //Button registerButton = createStyledButton("회원가입버튼");

        // 상태 라벨
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // 이벤트 핸들러
        loginButton.setOnAction(e -> handleLogin(userIdField.getText(), passwordField.getText(), statusLabel));
        //registerButton.setOnAction(e -> showRegisterDialog());

        // Enter 키로 로그인 - 기존 기능 유지
        passwordField.setOnAction(e -> handleLogin(userIdField.getText(), passwordField.getText(), statusLabel));

        // 폼에 컴포넌트 추가
        formContainer.getChildren().addAll(userIdField, passwordField, loginButton, statusLabel);
        mainContainer.getChildren().addAll(titleLabel, formContainer);

        return new Scene(mainContainer, 600, 1000); // 기존 크기 유지
    }

    private TextField createStyledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefSize(300, 50);
        field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px;"
        );

        // 호버 효과
        field.setOnMouseEntered(e -> field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2; " +
                        "-fx-font-size: 14px;"
        ));

        field.setOnMouseExited(e -> field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px;"
        ));

        return field;
    }

    private PasswordField createStyledPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setPrefSize(300, 50);
        field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px;"
        );

        // 호버 효과
        field.setOnMouseEntered(e -> field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2; " +
                        "-fx-font-size: 14px;"
        ));

        field.setOnMouseExited(e -> field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px;"
        ));

        return field;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 45);
        button.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: #afafaf; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 14px;"
        );

        // 호버 효과
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 14px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: #afafaf; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 14px;"
        ));

        button.setOnMousePressed(e -> button.setStyle(
                "-fx-background-color: #D3D3D3; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 14px;"
        ));

        return button;
    }

    private void handleLogin(String userId, String password, Label statusLabel) {
        if (userId.trim().isEmpty() || password.trim().isEmpty()) {
            statusLabel.setText("모두 값을 입력하세요");
            return;
        }

        statusLabel.setText("로그인 중...");
        statusLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");

        userService.login(userId, password,
                user -> Platform.runLater(() -> {
                    System.out.println("로그인 성공: " + user.getUserName());
                    showMainUI(user);
                }),
                error -> Platform.runLater(() -> {
                    statusLabel.setText("로그인 실패. 아이디와 비밀번호를 확인해주세요.");
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                })
        );
    }

    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(userService);
        dialog.show();
    }

    private void showMainUI(UserResponseDto  user) {
        if (userSession == null) {
            System.err.println("❌ userSession이 null입니다!");
            return;
        }
        // 1. 세션에 사용자 정보 저장
        userSession.setCurrentUser(user);

        // 2. Spring Bean으로 MainUIController 가져오기
        MainUIController mainController = applicationContext.getBean(MainUIController.class);

        // 3. Scene 생성 및 설정
        Scene mainScene = mainController.createMainScene(primaryStage);
        primaryStage.setScene(mainScene);
    }
}
