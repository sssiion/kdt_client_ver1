package org.example.kdt_bank_client2.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.kdt_bank_client2.DTO.ChatRoomResponseDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.Service.UserService;
import org.example.kdt_bank_client2.Service.ChatService;
import org.example.kdt_bank_client2.Session.UserSession;
import org.example.kdt_bank_client2.UI.LoginController;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatRoomListController {

    private final ChatService chatService;
    private final UserService userService;
    private final UserSession userSession;
    private final ApplicationContext applicationContext;
    @Setter
    private Stage primaryStage;
    private ObservableList<ChatRoomResponseDto> roomList = FXCollections.observableArrayList();
    private final ChatRoomController roomController;

    public Scene createChatRoomListScene(Stage stage) {
        setPrimaryStage(stage);

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
        UserResponseDto currentUser = userSession.getCurrentUser();
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
                    innerRoom(selectedRoom);
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
            List<ChatRoomResponseDto> rooms = roomController.getUserJoinRooms(userSession.getCurrentUser().getUserId());
            System.out.println(rooms);
            // 2단계: rooms null/empty 체크
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
                    roomController.createChatRoom(roomName.trim(), userSession.getCurrentUser().getUserId());
                    // 🔥 생성자 자동으로 방에 참가

                    //roomController.registerUserToRoom(newRoom.getRoomId(), userSession.getCurrentUser().getUserId());
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
    private void joinRoom(ChatRoomResponseDto room) throws Exception {
        roomController.registerUserToRoom(room.getRoomId(), userSession.getCurrentUser().getUserId());
    }

    private void innerRoom(ChatRoomResponseDto room) {
        try {
            // 🔥 1단계: 먼저 방 멤버로 등록
            //roomController.registerUserToRoom(room.getRoomId(), userSession.getCurrentUser().getUserId());

            // 🔥 2단계: 채팅방 UI로 이동
            ChatController chatController = applicationContext.getBean(ChatController.class);
            chatController.setPrimaryStage(primaryStage);
            Scene chatScene = chatController.createChatScene(primaryStage, room);
            primaryStage.setScene(chatScene);

        } catch (Exception e) {
            // "이미 참가" 오류는 무시하고 계속 진행
            if (e.getMessage().contains("이미 참가") || e.getMessage().contains("already")) {
                System.out.println("이미 참가한 방입니다. 채팅방으로 이동합니다.");
                // 채팅방으로 계속 이동
                ChatController chatController = applicationContext.getBean(ChatController.class);
                chatController.setPrimaryStage(primaryStage);
                Scene chatScene = chatController.createChatScene(primaryStage, room);
                primaryStage.setScene(chatScene);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("방 참가 실패");
                alert.setContentText("방에 참가할 수 없습니다: " + e.getMessage());
                alert.showAndWait();
            }
        }

    }

    private void handleLogout() {
        userService.logout();
        userSession.clearSession(); // 세션 정리 추가

        // 로그인 화면으로 돌아가기
        LoginController loginController = applicationContext.getBean(LoginController.class);
        Scene loginScene = loginController.createLoginScene(primaryStage);
        primaryStage.setScene(loginScene);
    }

    // MainUIController에서 사용할 수 있는 VBox 버전
    public VBox createChatListView() {
        // 사용자 세션 검증 추가
        UserResponseDto currentUser = userSession.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("사용자가 로그인되지 않았습니다.");
        }


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
                    innerRoom(selectedRoom);
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
            setText(null); // 중요: 텍스트를 비워 그래픽만 사용
            HBox container = new HBox(10);
            container.setPadding(new Insets(10));
            container.setStyle("-fx-background-color: white;");

            Label profileLabel = new Label();
            profileLabel.setPrefSize(40, 40);
            profileLabel.setMinSize(40, 40);
            profileLabel.setMaxSize(40, 40);
            profileLabel.setStyle(
                    "-fx-background-color: pink; " +
                            "-fx-background-radius: 20; " +
                            "-fx-border-radius: 20;"
            );

            VBox roomInfo = new VBox(2);
            Label roomNameLabel = new Label(room.getRoomName());
            roomNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label participantsLabel = new Label("참여자: " + room.getUserCount() + "명");
            participantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

            roomInfo.getChildren().addAll(roomNameLabel, participantsLabel);
            HBox.setHgrow(roomInfo, Priority.ALWAYS);

            container.getChildren().addAll(profileLabel, roomInfo);

            // 셀 높이가 너무 작게 계산되는 경우 방지
            container.setMinHeight(60);
            setMinHeight(Region.USE_PREF_SIZE);
            setPrefHeight(60);

            setGraphic(container);
        }
    }
}
