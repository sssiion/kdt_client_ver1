package org.example.kdt_bank_client2.Controller;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.Setter;
import org.example.kdt_bank_client2.DTO.ChatMessageDto;
import org.example.kdt_bank_client2.DTO.ChatRoomResponseDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;

import org.example.kdt_bank_client2.MainUIController;
import org.example.kdt_bank_client2.Service.UserService;
import org.example.kdt_bank_client2.Service.ChatService;
import org.example.kdt_bank_client2.Session.ChatRoomSession;
import org.example.kdt_bank_client2.Session.UserSession;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final UserSession userSession;
    private final ChatRoomSession roomSession;
    private final ApplicationContext applicationContext;
    private final ChatRoomController roomController;
    @Setter
    @Getter
    private Stage primaryStage;

    private VBox messageContainer;
    private ScrollPane messageScrollPane;
    private TextField messageField;
    private VBox functionPanel;
    private boolean isFunctionPanelVisible = false;


    public Scene createChatScene(Stage stage,ChatRoomResponseDto room) {
        // 세션에 방 정보 설정
        roomSession.setCurrentRoom(room);

        this.primaryStage = stage;
        UserResponseDto currentUser = userSession.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("사용자가 로그인되지 않았습니다.");
        }
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
        connectWebSocketAndEnterRoom();

        return new Scene(mainContainer, 600, 1000); // 기존 크기 유지
    }

    private VBox createTopPanel() {
        String roomName = roomSession.getCurrentRoomName();
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

        Label titleLabel = new Label(roomName);
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
        messageField.setOnAction(e -> {
            String content = messageField.getText().trim();
            String roomId = roomSession.getCurrentRoomId();
            UserResponseDto user = userSession.getCurrentUser();
            ChatMessageDto myMessage = new ChatMessageDto();
            myMessage.setUserId(user.getUserId());
            myMessage.setContent(content);
            myMessage.setType("CHAT");
            addMessageBubble(myMessage);

            // 🔥 수정: ChatService에 위임
            chatService.sendMessage(roomId, user, content);
            messageField.clear();
        });
        HBox.setHgrow(messageField, Priority.ALWAYS);



        inputPanel.getChildren().addAll(menuButton, messageField);

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
                inviteUserToRoom(friendId);
            }
        });

        dialog.showAndWait();
    }

    private void inviteUserToRoom(String friendId) {
        // 서버에 친구 초대 요청 (기존 chatClient.sendMessage("/join "+roomId+" "+fid) 로직)
        try {
            // ChatService를 통해 초대 메시지 전송
            // 실제 구현은 서버 API에 따라 달라질 수 있음

            String roomId = roomSession.getCurrentRoomId();
            roomController.registerUserToRoom(roomId,friendId);
            System.out.println("친구 초대: " + friendId + " to room: " + roomId);

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

    }


    public enum MessageType{
        CHAT, JOIN, LEAVE;
        public static MessageType fromString(String type) {
            if (type == null) return CHAT;

            try {
                return MessageType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return CHAT;  // 잘못된 값이면 CHAT으로 기본 설정
            }
        }
    }


    private void hideFunctionPanel() {
        functionPanel.getChildren().clear();
        functionPanel.setPrefHeight(0);
        functionPanel.setVisible(false);
        isFunctionPanelVisible = false;
    }



    private void showErrorAndBack(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("연결 오류");
        alert.setContentText(msg);
        alert.showAndWait();
        goBackToRoomList();
    }
    private void loadPreviousMessages(String roomId) {
        try{
            System.out.println("이전 메시지 로드 중...");

            // ChatRoomController를 통해 기존 메시지 가져오기
            List<ChatMessageDto> previousMessages = roomController.getMessage(roomId);

            if (previousMessages != null && !previousMessages.isEmpty()) {
                System.out.println("로드된 메시지 개수: " + previousMessages.size());

                // UI 스레드에서 메시지들을 화면에 추가
                Platform.runLater(() -> {
                    for (ChatMessageDto message : previousMessages) {
                        addMessageBubble(message);
                    }
                    System.out.println("✅ 이전 메시지 로드 완료");
                });
            } else {
                System.out.println("이전 메시지가 없습니다.");
            }
        } catch (Exception e) {
            System.err.println("이전 메시지 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }


    }


    private void connectWebSocketAndEnterRoom() {
        String userId = userSession.getCurrentUser().getUserId();
        String roomId = roomSession.getCurrentRoomId();
        UserResponseDto user = userSession.getCurrentUser();
        loadPreviousMessages(roomId);
        if (!chatService.isConnected()) {
            chatService.connectWebSocket(
                    userId,
                    () -> {
                        // 🔥 수정: ChatService.joinRoom() 호출
                        chatService.enterRoom(
                                roomId,
                                user,
                                message -> Platform.runLater(() -> {
                                    // ✅ 자신이 보낸 메시지가 아닐 때만 표시
                                    if (!message.getUserId().equals(userSession.getCurrentUser().getUserId())) {
                                        addMessageBubble(message);
                                    } else {
                                        System.out.println("자신의 메시지는 이미 표시됨 - 스킵: " + message.getContent());
                                    }
                                }),
                                history -> Platform.runLater(() -> System.out.println("히스토리: " + history)),
                                error -> Platform.runLater(() -> showErrorAndBack(error))

                        );
                    },
                    error -> Platform.runLater(() -> showErrorAndBack("WebSocket 연결 실패: " + error))
            );
        } else {
            // 🔥 수정: ChatService.joinRoom() 호출
            chatService.enterRoom(
                    roomId,
                    user,
                    message -> Platform.runLater(() -> addMessageBubble(message)),
                    history -> Platform.runLater(() -> System.out.println("히스토리: " + history)),
                    error -> Platform.runLater(() -> showErrorAndBack(error))
            );
        }
    }


    // 수정된 메서드
    private void addMessageBubble(ChatMessageDto message) {
        String bubbleId = System.currentTimeMillis() + "-" + message.getUserId();
        String displayName = getUserDisplayName(message.getUserId());

        ChatMessageBubble bubble;

        if (message.getUserId().equals(userSession.getCurrentUser().getUserId())) {
            bubble = new MyMessageBubble(displayName, message.getContent(), bubbleId);
        } else {
            bubble = new SenderMessageBubble(displayName, message.getContent(), bubbleId);
        }

        //bubbleMap.put(bubbleId, bubble);
        messageContainer.getChildren().add(bubble.getMessageComponent());
        messageScrollPane.setVvalue(1.0);
    }

    private String getUserDisplayName(String userId) {
        if (userId.equals(userSession.getCurrentUser().getUserId())) {
            return userSession.getCurrentUser().getUserName();
        }
        return userId; // 임시로 userId 반환
    }


    private void goBackToRoomList() {
        // 방 나가기

        if (chatService.isConnected()) {
            String roomId = roomSession.getCurrentRoomId();
            UserResponseDto user = userSession.getCurrentUser();
            // 🔥 수정: ChatService.leaveRoom() 호출
            chatService.exitChatRoom(roomId, user);
        }
        roomSession.leaveRoom();
        MainUIController mainController = applicationContext.getBean(MainUIController.class);
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


            VBox textBox = new VBox();
            textBox.setAlignment(Pos.CENTER_LEFT);
            textBox.getChildren().addAll(nameLabel, bubbleLabel);

            messageRow.getChildren().addAll(textBox);
            messageBox.getChildren().add(messageRow);

            return messageBox;
        }
    }
}
