package org.example.kdt_bank_client2;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Setter;
import org.example.kdt_bank_client2.AuthService;
import org.example.kdt_bank_client2.ChatService;
import org.example.kdt_bank_client2.DTO.ChatMessageDto;
import org.example.kdt_bank_client2.DTO.ChatRoomResponseDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChatController {

    private final ChatService chatService;
    private final AuthService authService;

    private final UserResponseDto currentUser;
    private final ChatRoomResponseDto chatRoom;
    private Stage primaryStage;

    private VBox messageContainer;
    private ScrollPane messageScrollPane;
    private TextField messageField;
    private VBox functionPanel;
    private boolean isFunctionPanelVisible = false;

    // 메시지 버블 관리
    private Map<String, ChatMessageBubble> bubbleMap;

    public ChatController(ChatService chatService, AuthService authService,
                          UserResponseDto currentUser, ChatRoomResponseDto chatRoom) {
        this.chatService = chatService;
        this.authService = authService;
        this.currentUser = currentUser;
        this.chatRoom = chatRoom;
        this.bubbleMap = new HashMap<>();
    }

    public Scene createChatScene(Stage stage) {
        this.primaryStage = stage;

        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: white;");

        // 상단 헤더 (기존 topPanel 스타일)
        VBox topPanel = createTopPanel();
        mainContainer.setTop(topPanel);

        // 중앙 메시지 영역
        createMessageArea();
        mainContainer.setCenter(messageScrollPane);

        // 하단 입력 영역 + 기능 패널
        VBox bottomWrapper = createBottomArea();
        mainContainer.setBottom(bottomWrapper);

        // WebSocket 연결 및 방 참가
        connectAndJoinRoom();

        return new Scene(mainContainer, 600, 1000); // 기존 크기 유지
    }

    private VBox createTopPanel() {
        VBox topPanel = new VBox();
        topPanel.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        topPanel.setPadding(new Insets(10));

        // 상단 버튼 영역
        HBox headerButtons = new HBox();
        headerButtons.setAlignment(Pos.CENTER_LEFT);
        headerButtons.setSpacing(10);

        Button backButton = new Button("←");
        backButton.setStyle("-fx-background-color: transparent; -fx-font-size: 20px;");
        backButton.setOnAction(e -> goBackToRoomList());

        Label titleLabel = new Label(chatRoom.getRoomName());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Malgun Gothic';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button searchButton = new Button("🔍");
        searchButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");

        headerButtons.getChildren().addAll(backButton, titleLabel, spacer, searchButton);

        // 친구 초대 버튼 (기존 invitebutton 기능)
        Button inviteButton = createStyledButton("친구초대");
        inviteButton.setOnAction(e -> showInviteDialog());

        // 검색바 (기존 searchbar 기능)
        TextField searchBar = new TextField();
        searchBar.setPromptText("메시지 검색...");
        searchBar.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0;"
        );

        topPanel.getChildren().addAll(headerButtons, inviteButton, searchBar);
        return topPanel;
    }

    private void createMessageArea() {
        messageContainer = new VBox();
        messageContainer.setSpacing(4);
        messageContainer.setPadding(new Insets(10));
        messageContainer.setStyle("-fx-background-color: white;");

        messageScrollPane = new ScrollPane(messageContainer);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setStyle("-fx-background-color: white; -fx-border-width: 0;");
        messageScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // 스크롤 영역 클릭 시 기능 패널 숨기기
        messageScrollPane.setOnMousePressed(e -> hideFunctionPanel());
    }

    private VBox createBottomArea() {
        VBox bottomWrapper = new VBox();

        // 입력 패널 (기존 inputPanel 스타일)
        HBox inputPanel = new HBox();
        inputPanel.setSpacing(10);
        inputPanel.setPadding(new Insets(8, 10, 8, 10));
        inputPanel.setStyle("-fx-background-color: #E8EDF5;");
        inputPanel.setAlignment(Pos.CENTER);

        // 메뉴 버튼 (기존 menuButton)
        Button menuButton = new Button("☰");
        menuButton.setStyle("-fx-background-color: transparent; -fx-font-size: 20px;");
        menuButton.setOnAction(e -> toggleFunctionPanel());

        // 메시지 입력 필드
        messageField = new TextField();
        messageField.setPromptText("메시지를 입력하세요...");
        messageField.setStyle(
                "-fx-background-color: #E8EDF5; " +
                        "-fx-border-width: 0; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-family: 'Malgun Gothic';"
        );
        messageField.setOnAction(e -> sendMessage());
        HBox.setHgrow(messageField, Priority.ALWAYS);

        // 이모지 버튼
        Button emojiButton = new Button("😊");
        emojiButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
        emojiButton.setOnAction(e -> showEmojiPanel());

        inputPanel.getChildren().addAll(menuButton, messageField, emojiButton);

        // 기능 패널 (기존 functionPanel)
        functionPanel = new VBox();
        functionPanel.setPrefHeight(0);
        functionPanel.setStyle("-fx-background-color: #f0f0f0;");
        functionPanel.setVisible(false);

        bottomWrapper.getChildren().addAll(inputPanel, functionPanel);
        return bottomWrapper;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: #afafaf; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black;"
        );

        // 호버 효과
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: #afafaf; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black;"
        ));

        button.setOnMousePressed(e -> button.setStyle(
                "-fx-background-color: #D3D3D3; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black;"
        ));

        return button;
    }

    private void showInviteDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("친구 초대");
        dialog.setHeaderText("초대할 친구 ID를 입력하세요");

        ButtonType inviteButtonType = new ButtonType("친구추가하기", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(inviteButtonType, ButtonType.CANCEL);

        TextField friendIdField = new TextField();
        friendIdField.setPromptText("초대할 친구id를 입력하세요");
        friendIdField.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0;"
        );

        dialog.getDialogPane().setContent(friendIdField);

        Button inviteButton = (Button) dialog.getDialogPane().lookupButton(inviteButtonType);
        inviteButton.setOnAction(e -> {
            String friendId = friendIdField.getText().trim();
            if (!friendId.isEmpty()) {
                // 친구 초대 로직 (기존 /join 명령어 사용)
                inviteToRoom(friendId);
            }
        });

        dialog.showAndWait();
    }

    private void inviteToRoom(String friendId) {
        // 서버에 친구 초대 요청 (기존 chatClient.sendMessage("/join "+roomId+" "+fid) 로직)
        try {
            // ChatService를 통해 초대 메시지 전송
            // 실제 구현은 서버 API에 따라 달라질 수 있음
            System.out.println("친구 초대: " + friendId + " to room: " + chatRoom.getRoomId());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("초대 완료");
            alert.setContentText(friendId + "님을 초대했습니다.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("초대 실패");
            alert.setContentText("친구 초대에 실패했습니다: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void toggleFunctionPanel() {
        isFunctionPanelVisible = !isFunctionPanelVisible;

        if (isFunctionPanelVisible) {
            showMenuFunctionPanel();
        } else {
            hideFunctionPanel();
        }
    }

    private void showMenuFunctionPanel() {
        functionPanel.getChildren().clear();
        functionPanel.setPrefHeight(200);

        HBox menuOptions = new HBox();
        menuOptions.setSpacing(20);
        menuOptions.setAlignment(Pos.CENTER);
        menuOptions.setPadding(new Insets(20));

        // 기존 luckyDrawBtn, voteBtn, whisperBtn 기능
        Button luckyDrawBtn = new Button("🎲\n추첨");
        Button voteBtn = new Button("🗳️\n투표");
        Button whisperBtn = new Button("💬\n귓속말");

        Button[] buttons = {luckyDrawBtn, voteBtn, whisperBtn};
        for (Button btn : buttons) {
            btn.setPrefSize(80, 80);
            btn.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #ddd; " +
                            "-fx-border-width: 1; " +
                            "-fx-background-radius: 10;"
            );
        }

        luckyDrawBtn.setOnAction(e -> showLuckyDrawPanel());
        voteBtn.setOnAction(e -> showVotePanel());
        whisperBtn.setOnAction(e -> showWhisperPanel());

        menuOptions.getChildren().addAll(buttons);
        functionPanel.getChildren().add(menuOptions);
        functionPanel.setVisible(true);
    }

    private void showEmojiPanel() {
        functionPanel.getChildren().clear();
        functionPanel.setPrefHeight(150);

        GridPane emojiGrid = new GridPane();
        emojiGrid.setHgap(10);
        emojiGrid.setVgap(10);
        emojiGrid.setPadding(new Insets(20));
        emojiGrid.setAlignment(Pos.CENTER);

        String[] emojis = {"😊", "😂", "😍", "🥰", "😭", "😱", "👍", "👎", "❤️", "🔥"};

        int col = 0, row = 0;
        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setPrefSize(40, 40);
            emojiBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 20px;");
            emojiBtn.setOnAction(e -> {
                messageField.setText(messageField.getText() + emoji);
                messageField.requestFocus();
            });

            emojiGrid.add(emojiBtn, col, row);
            col++;
            if (col >= 5) {
                col = 0;
                row++;
            }
        }

        functionPanel.getChildren().add(emojiGrid);
        functionPanel.setVisible(true);
    }

    private void showLuckyDrawPanel() {
        functionPanel.getChildren().clear();
        functionPanel.setPrefHeight(200);

        VBox luckyPanel = new VBox();
        luckyPanel.setSpacing(10);
        luckyPanel.setPadding(new Insets(20));
        luckyPanel.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("추첨 기능");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField itemField = new TextField();
        itemField.setPromptText("추첨할 항목들을 입력하세요 (쉼표로 구분)");

        Button drawButton = createStyledButton("추첨하기");
        drawButton.setOnAction(e -> {
            String items = itemField.getText();
            if (!items.isEmpty()) {
                // 추첨 로직 구현
                performLuckyDraw(items);
            }
        });

        luckyPanel.getChildren().addAll(titleLabel, itemField, drawButton);
        functionPanel.getChildren().add(luckyPanel);
        functionPanel.setVisible(true);
    }

    private void showVotePanel() {
        functionPanel.getChildren().clear();
        functionPanel.setPrefHeight(300);

        VBox votePanel = new VBox();
        votePanel.setSpacing(10);
        votePanel.setPadding(new Insets(20));
        votePanel.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("투표 만들기");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField questionField = new TextField();
        questionField.setPromptText("투표 질문을 입력하세요");

        TextArea optionsArea = new TextArea();
        optionsArea.setPromptText("선택지들을 입력하세요 (한 줄에 하나씩)");
        optionsArea.setPrefRowCount(3);

        Button createVoteButton = createStyledButton("투표 만들기");
        createVoteButton.setOnAction(e -> {
            String question = questionField.getText();
            String options = optionsArea.getText();
            if (!question.isEmpty() && !options.isEmpty()) {
                // 투표 생성 로직 구현
                createVote(question, options);
            }
        });

        votePanel.getChildren().addAll(titleLabel, questionField, optionsArea, createVoteButton);
        functionPanel.getChildren().add(votePanel);
        functionPanel.setVisible(true);
    }

    private void showWhisperPanel() {
        // 귓속말 기능 패널 (추후 구현)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("귓속말");
        alert.setContentText("귓속말 기능은 추후 구현 예정입니다.");
        alert.showAndWait();
        hideFunctionPanel();
    }

    private void hideFunctionPanel() {
        functionPanel.getChildren().clear();
        functionPanel.setPrefHeight(0);
        functionPanel.setVisible(false);
        isFunctionPanelVisible = false;
    }

    private void performLuckyDraw(String items) {
        // 추첨 로직 구현
        String[] itemArray = items.split(",");
        if (itemArray.length > 0) {
            int randomIndex = (int) (Math.random() * itemArray.length);
            String winner = itemArray[randomIndex].trim();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("추첨 결과");
            alert.setContentText("🎉 당첨: " + winner);
            alert.showAndWait();
        }
        hideFunctionPanel();
    }

    private void createVote(String question, String options) {
        // 투표 생성 로직 구현
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("투표 생성됨");
        alert.setContentText("투표가 생성되었습니다: " + question);
        alert.showAndWait();
        hideFunctionPanel();
    }

    private void connectAndJoinRoom() {
        if (!chatService.isConnected()) {
            chatService.connectWebSocket(currentUser.getUserId(),
                    () -> joinRoom(),
                    error -> Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("연결 오류");
                        alert.setContentText("WebSocket 연결에 실패했습니다: " + error);
                        alert.showAndWait();
                        goBackToRoomList();
                    })
            );
        } else {
            joinRoom();
        }
    }

    private void joinRoom() {
        chatService.joinRoom(chatRoom.getRoomId(), currentUser,
                message -> Platform.runLater(() -> {
                    addMessageBubble(message);
                }),
                history -> Platform.runLater(() -> {
                    System.out.println("채팅 히스토리: " + history);
                }),
                error -> Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("오류");
                    alert.setContentText("방 참가 오류: " + error);
                    alert.showAndWait();
                })
        );
    }

    private void addMessageBubble(ChatMessageDto message) {
        String bubbleId = System.currentTimeMillis() + "-" + message.getSenderId();

        ChatMessageBubble bubble;
        if (message.getSenderId().equals(currentUser.getUserId())) {
            bubble = new MyMessageBubble(message.getSenderName(), message.getMessage(), bubbleId);
        } else {
            bubble = new SenderMessageBubble(message.getSenderName(), message.getMessage(), bubbleId);
        }

        bubbleMap.put(bubbleId, bubble);
        messageContainer.getChildren().add(bubble.getMessageComponent());

        // 스크롤을 맨 아래로
        Platform.runLater(() -> {
            messageScrollPane.setVvalue(1.0);
        });
    }

    private void sendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty()) {
            chatService.sendMessage(chatRoom.getRoomId(), currentUser, messageText);
            messageField.clear();
        }
    }

    private void goBackToRoomList() {
        // 방 나가기
        if (chatService.isConnected()) {
            chatService.leaveRoom(chatRoom.getRoomId(), currentUser);
        }

        // 메인 UI로 돌아가기
        MainUIController mainController = new MainUIController(chatService, authService, currentUser);
        Scene mainScene = mainController.createMainScene(primaryStage);
        primaryStage.setScene(mainScene);
    }

    // 메시지 버블 인터페이스 및 구현 클래스들
    public interface ChatMessageBubble {
        VBox getMessageComponent();
        String getText();
        void setText(String text);
    }

    public class MyMessageBubble implements ChatMessageBubble {
        private String name;
        private String text;
        private String id;

        public MyMessageBubble(String name, String text, String id) {
            this.name = name;
            this.text = text;
            this.id = id;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public VBox getMessageComponent() {
            VBox messageBox = new VBox();
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setSpacing(2);

            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-family: 'Malgun Gothic';");

            Label bubbleLabel = new Label(text);
            bubbleLabel.setStyle(
                    "-fx-background-color: #FFE54C; " +  // 노란색 (기존 Color(255, 229, 76))
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 10 15 10 15; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-family: 'Malgun Gothic';"
            );
            bubbleLabel.setWrapText(true);
            bubbleLabel.setMaxWidth(300);

            HBox messageRow = new HBox();
            messageRow.setAlignment(Pos.CENTER_RIGHT);
            messageRow.setSpacing(5);

            // 프로필 (오른쪽)
            Label profile = new Label();
            profile.setPrefSize(40, 40);
            profile.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

            VBox textBox = new VBox();
            textBox.setAlignment(Pos.CENTER_RIGHT);
            textBox.getChildren().addAll(nameLabel, bubbleLabel);

            messageRow.getChildren().addAll(textBox, profile);
            messageBox.getChildren().add(messageRow);

            return messageBox;
        }
    }

    public class SenderMessageBubble implements ChatMessageBubble {
        private String name;
        private String text;
        private String id;

        public SenderMessageBubble(String name, String text, String id) {
            this.name = name;
            this.text = text;
            this.id = id;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public VBox getMessageComponent() {
            VBox messageBox = new VBox();
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.setSpacing(2);

            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-family: 'Malgun Gothic';");

            Label bubbleLabel = new Label(text);
            bubbleLabel.setStyle(
                    "-fx-background-color: #E6E6E6; " +  // 회색 (기존 Color(230, 230, 230))
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 10 15 10 15; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-family: 'Malgun Gothic';"
            );
            bubbleLabel.setWrapText(true);
            bubbleLabel.setMaxWidth(300);

            HBox messageRow = new HBox();
            messageRow.setAlignment(Pos.CENTER_LEFT);
            messageRow.setSpacing(5);

            // 프로필 (왼쪽)
            Label profile = new Label();
            profile.setPrefSize(40, 40);
            profile.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

            VBox textBox = new VBox();
            textBox.setAlignment(Pos.CENTER_LEFT);
            textBox.getChildren().addAll(nameLabel, bubbleLabel);

            messageRow.getChildren().addAll(profile, textBox);
            messageBox.getChildren().add(messageRow);

            return messageBox;
        }
    }
}
