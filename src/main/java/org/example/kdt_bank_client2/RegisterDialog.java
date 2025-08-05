package org.example.kdt_bank_client2;


import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.springframework.stereotype.Component;

@Component
public class RegisterDialog {

    private final AuthService authService;

    public RegisterDialog(AuthService authService) {
        this.authService = authService;
    }

    public void show() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("회원가입");
        dialog.setHeaderText("새 계정을 만들어보세요");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // 버튼 타입 설정
        ButtonType registerButtonType = new ButtonType("가입하기", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        // 폼 생성
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 입력 필드들
        TextField userIdField = createStyledTextField("사용자 ID");
        PasswordField passwordField = createStyledPasswordField("비밀번호");
        TextField userNameField = createStyledTextField("이름");
        TextField userPhoneField = createStyledTextField("전화번호 (선택사항)");

        // 상태 라벨
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // GridPane에 컴포넌트 추가
        grid.add(new Label("사용자 ID:"), 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(new Label("비밀번호:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("이름:"), 0, 2);
        grid.add(userNameField, 1, 2);
        grid.add(new Label("전화번호:"), 0, 3);
        grid.add(userPhoneField, 1, 3);
        grid.add(statusLabel, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // 가입하기 버튼 이벤트
        Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.setStyle(createButtonStyle());

        registerButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            String password = passwordField.getText();
            String userName = userNameField.getText().trim();
            String userPhone = userPhoneField.getText().trim();

            if (userId.isEmpty() || password.isEmpty() || userName.isEmpty()) {
                statusLabel.setText("필수 정보를 모두 입력해주세요.");
                e.consume(); // 다이얼로그 닫기 방지
                return;
            }

            // 입력값 검증
            if (!isValidUserId(userId)) {
                statusLabel.setText("사용자 ID는 4-20자의 영문, 숫자만 가능합니다.");
                e.consume();
                return;
            }

            if (!isValidPassword(password)) {
                statusLabel.setText("비밀번호는 최소 6자 이상이어야 합니다.");
                e.consume();
                return;
            }

            if (!isValidUserName(userName)) {
                statusLabel.setText("이름은 2-20자의 한글, 영문만 가능합니다.");
                e.consume();
                return;
            }

            if (!userPhone.isEmpty() && !isValidPhone(userPhone)) {
                statusLabel.setText("올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)");
                e.consume();
                return;
            }

            // 회원가입 처리
            statusLabel.setText("회원가입 처리 중...");
            statusLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");

            authService.register(userId, password, userName, userPhone,
                    () -> {
                        // 성공 시
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("회원가입 완료");
                        alert.setHeaderText("회원가입이 성공적으로 완료되었습니다!");
                        alert.setContentText("이제 로그인할 수 있습니다.\n아이디: " + userId + "\n이름: " + userName);
                        alert.showAndWait();
                        dialog.close();
                    },
                    error -> {
                        // 실패 시
                        statusLabel.setText(error);
                        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                        e.consume();
                    }
            );
        });

        // 취소 버튼 스타일링
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText("취소");
        cancelButton.setStyle(createCancelButtonStyle());

        // Enter 키로 회원가입
        userPhoneField.setOnAction(e -> registerButton.fire());

        dialog.showAndWait();
    }

    private TextField createStyledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefWidth(200);
        field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10;"
        );

        // 포커스 효과
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.setStyle(
                        "-fx-background-color: #afafaf; " +
                                "-fx-background-radius: 25; " +
                                "-fx-border-radius: 25; " +
                                "-fx-border-color: black; " +
                                "-fx-border-width: 2; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 10;"
                );
            } else {
                field.setStyle(
                        "-fx-background-color: #afafaf; " +
                                "-fx-background-radius: 25; " +
                                "-fx-border-radius: 25; " +
                                "-fx-border-width: 0; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 10;"
                );
            }
        });

        return field;
    }

    private PasswordField createStyledPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setPrefWidth(200);
        field.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10;"
        );

        // 포커스 효과
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                field.setStyle(
                        "-fx-background-color: #afafaf; " +
                                "-fx-background-radius: 25; " +
                                "-fx-border-radius: 25; " +
                                "-fx-border-color: black; " +
                                "-fx-border-width: 2; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 10;"
                );
            } else {
                field.setStyle(
                        "-fx-background-color: #afafaf; " +
                                "-fx-background-radius: 25; " +
                                "-fx-border-radius: 25; " +
                                "-fx-border-width: 0; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 10;"
                );
            }
        });

        return field;
    }

    private String createButtonStyle() {
        return "-fx-background-color: #28a745; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 25; " +
                "-fx-border-radius: 25; " +
                "-fx-padding: 10 20 10 20;";
    }

    private String createCancelButtonStyle() {
        return "-fx-background-color: #dc3545; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-background-radius: 25; " +
                "-fx-border-radius: 25; " +
                "-fx-padding: 10 20 10 20;";
    }

    // 입력값 검증 메서드들
    private boolean isValidUserId(String userId) {
        // 4-20자의 영문, 숫자만 허용
        return userId.matches("^[a-zA-Z0-9]{4,20}$");
    }

    private boolean isValidPassword(String password) {
        // 최소 6자 이상
        return password.length() >= 6;
    }

    private boolean isValidUserName(String userName) {
        // 2-20자의 한글, 영문만 허용
        return userName.matches("^[가-힣a-zA-Z]{2,20}$");
    }

    private boolean isValidPhone(String phone) {
        // 010-1234-5678 또는 01012345678 형식
        return phone.matches("^010-?\\d{4}-?\\d{4}$");
    }
}
