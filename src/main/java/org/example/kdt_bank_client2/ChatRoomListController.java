package org.example.kdt_bank_client2;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.example.kdt_bank_client2.DTO.ChatRoomResponseDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomListController {

    private final ChatService chatService;
    private final AuthService authService;
    private final UserResponseDto currentUser;
    private Stage primaryStage;
    private ObservableList<ChatRoomResponseDto> roomList;
    private ChatRoomController roomController;

    public ChatRoomListController(ChatService chatService, AuthService authService, UserResponseDto currentUser) {
        this.chatService = chatService;
        this.authService = authService;
        this.currentUser = currentUser;
        this.roomList = FXCollections.observableArrayList();
        // Spring에서 주입받을 수 있도록 수정 필요
        this.roomController = new ChatRoomController();
    }

    public Scene createChatRoomListScene(Stage stage) {
        this.primaryStage = stage;

        // 메인 컨테이너
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #f0f2f5;");

        // 상단 헤더
        HBox header = createHeader();
        mainContainer.setTop(header);

        // 중앙 채팅방 목록
        VBox centerContent = createCenterContent();
        mainContainer.setCenter(centerContent);

        // 하단 버튼들
        HBox bottomButtons = createBottomButtons();
        mainContainer.setBottom(bottomButtons);

        // 채팅방 목록 로드
        loadChatRooms();

        return new Scene(mainContainer, 800, 600);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #1877f2;");

        Label titleLabel = new Label("채팅방 목록");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("안녕하세요, " + currentUser.getUserName() + "님!");
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Button logoutButton = new Button("로그아웃");
        logoutButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
        logoutButton.setOnAction(e -> handleLogout());

        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return header;
    }

    private VBox createCenterContent() {
        VBox centerContent = new VBox(10);
        centerContent.setPadding(new Insets(20));

        // 검색 바
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("채팅방 검색...");
        searchField.setPrefHeight(35);

        Button searchButton = new Button("검색");
        searchButton.setPrefHeight(35);
        searchButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        searchButton.setOnAction(e -> searchRooms(searchField.getText()));

        searchBox.getChildren().addAll(searchField, searchButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // 채팅방 테이블
        TableView<ChatRoomResponseDto> tableView = new TableView<>();
        tableView.setItems(roomList);

        TableColumn<ChatRoomResponseDto, String> roomNameCol = new TableColumn<>("방 이름");
        roomNameCol.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        roomNameCol.setPrefWidth(300);

        TableColumn<ChatRoomResponseDto, Integer> userCountCol = new TableColumn<>("참여자 수");
        userCountCol.setCellValueFactory(new PropertyValueFactory<>("userCount"));
        userCountCol.setPrefWidth(100);

        TableColumn<ChatRoomResponseDto, String> createdAtCol = new TableColumn<>("생성일");
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtCol.setPrefWidth(200);

        tableView.getColumns().addAll(roomNameCol, userCountCol, createdAtCol);

        // 더블클릭으로 방 입장
        tableView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ChatRoomResponseDto selectedRoom = tableView.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    joinRoom(selectedRoom);
                }
            }
        });

        centerContent.getChildren().addAll(searchBox, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return centerContent;
    }

    private HBox createBottomButtons() {
        HBox bottomButtons = new HBox(10);
        bottomButtons.setPadding(new Insets(15));
        bottomButtons.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");

        Button createRoomButton = new Button("새 방 만들기");
        createRoomButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createRoomButton.setOnAction(e -> showCreateRoomDialog());

        Button refreshButton = new Button("새로고침");
        refreshButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 14px;");
        refreshButton.setOnAction(e -> loadChatRooms());

        bottomButtons.getChildren().addAll(createRoomButton, refreshButton);

        return bottomButtons;
    }

    private void loadChatRooms() {
        try {
            var rooms = roomController.getAllRooms();
            Platform.runLater(() -> {
                roomList.clear();
                roomList.addAll(rooms);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setContentText("채팅방 목록을 불러오는데 실패했습니다: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    private void searchRooms(String keyword) {
        if (keyword.trim().isEmpty()) {
            loadChatRooms();
            return;
        }

        try {
            var rooms = roomController.searchRooms(keyword.trim());
            Platform.runLater(() -> {
                roomList.clear();
                roomList.addAll(rooms);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setContentText("검색에 실패했습니다: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    private void showCreateRoomDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("새 채팅방 만들기");
        dialog.setHeaderText("새 채팅방을 만듭니다");
        dialog.setContentText("방 이름을 입력하세요:");

        dialog.showAndWait().ifPresent(roomName -> {
            if (!roomName.trim().isEmpty()) {
                try {
                    roomController.createRoom(roomName.trim());
                    loadChatRooms();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("성공");
                    alert.setContentText("채팅방이 생성되었습니다!");
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("오류");
                    alert.setContentText("채팅방 생성에 실패했습니다: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    private void joinRoom(ChatRoomResponseDto room) {
        ChatController chatController = new ChatController(chatService, authService, currentUser, room);
        Scene chatScene = chatController.createChatScene(primaryStage);
        primaryStage.setScene(chatScene);
    }

    private void handleLogout() {
        authService.logout();

        // 로그인 화면으로 돌아가기
        LoginController loginController = new LoginController(authService, chatService);
        Scene loginScene = loginController.createLoginScene(primaryStage);
        primaryStage.setScene(loginScene);
    }

    // MainUIController에서 사용할 수 있는 VBox 버전
    public VBox createChatListView() {
        VBox chatListView = new VBox();
        chatListView.setSpacing(10);
        chatListView.setPadding(new Insets(20));

        // 검색 바
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("채팅방 검색...");
        searchField.setPrefHeight(35);

        Button searchButton = new Button("검색");
        searchButton.setPrefHeight(35);
        searchButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        searchButton.setOnAction(e -> searchRooms(searchField.getText()));

        searchBox.getChildren().addAll(searchField, searchButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // 새 방 만들기 버튼
        Button createRoomButton = new Button("새 방 만들기");
        createRoomButton.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-color: #afafaf; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black;"
        );
        createRoomButton.setOnAction(e -> showCreateRoomDialog());

        // 채팅방 목록
        ListView<ChatRoomResponseDto> listView = new ListView<>();
        listView.setItems(roomList);
        listView.setCellFactory(listView2 -> new ChatRoomListCell());

        // 더블클릭으로 방 입장
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ChatRoomResponseDto selectedRoom = listView.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    joinRoom(selectedRoom);
                }
            }
        });

        chatListView.getChildren().addAll(searchBox, createRoomButton, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        // 채팅방 목록 로드
        loadChatRooms();

        return chatListView;
    }

    // 채팅방 목록 셀 커스텀 클래스
    private static class ChatRoomListCell extends ListCell<ChatRoomResponseDto> {
        @Override
        protected void updateItem(ChatRoomResponseDto room, boolean empty) {
            super.updateItem(room, empty);

            if (empty || room == null) {
                setGraphic(null);
                return;
            }

            HBox container = new HBox();
            container.setSpacing(10);
            container.setPadding(new Insets(10));
            container.setStyle("-fx-background-color: white;");

            // 프로필 (기존 RoundedPanel 대신)
            Label profileLabel = new Label();
            profileLabel.setPrefSize(60, 60);
            profileLabel.setStyle(
                    "-fx-background-color: pink; " +
                            "-fx-background-radius: 30; " +
                            "-fx-border-radius: 30;"
            );

            // 채팅방 정보
            VBox roomInfo = new VBox();
            roomInfo.setSpacing(2);

            Label roomNameLabel = new Label(room.getRoomName());
            roomNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: '맑은 고딕';");

            Label participantsLabel = new Label("참여자: " + room.getUserCount() + "명");
            participantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

            roomInfo.getChildren().addAll(roomNameLabel, participantsLabel);

            container.getChildren().addAll(profileLabel, roomInfo);
            HBox.setHgrow(roomInfo, Priority.ALWAYS);

            setGraphic(container);
        }
    }
}
