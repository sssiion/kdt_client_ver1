package org.example.kdt_bank_client2;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Component
public class FriendListController {

    private final UserResponseDto currentUser;
    private final ChatService chatService;
    private VBox friendListContainer;
    private Map<String, Boolean> friendData;

    public FriendListController(UserResponseDto currentUser, ChatService chatService) {
        this.currentUser = currentUser;
        this.chatService = chatService;
        this.friendData = new HashMap<>();
    }

    public VBox createFriendListView() {
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: white;");
        mainContainer.setSpacing(10);

        // 검색바 (기존 searchbar 기능)
        TextField searchBar = new TextField();
        searchBar.setPromptText("친구 검색...");
        searchBar.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-padding: 10;"
        );

        // 친구 추가 버튼 (기존 friendplus 기능)
        Button addFriendButton = createStyledButton("친구추가");
        addFriendButton.setOnAction(e -> showAddFriendDialog());

        // 친구 목록 컨테이너
        friendListContainer = new VBox();
        friendListContainer.setSpacing(5);

        ScrollPane scrollPane = new ScrollPane(friendListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-width: 0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // 임시 친구 데이터 (실제로는 서버에서 받아올 데이터)
        loadFriendData();

        mainContainer.getChildren().addAll(searchBar, addFriendButton, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainContainer;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
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

    private void showAddFriendDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("친구 추가");
        dialog.setHeaderText("친구 이름을 입력하세요");

        ButtonType addButtonType = new ButtonType("친구 추가하기", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField friendNameField = new TextField();
        friendNameField.setPromptText("친구 이름을 입력하세요");
        friendNameField.setStyle(
                "-fx-background-color: #afafaf; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-padding: 10;"
        );

        dialog.getDialogPane().setContent(friendNameField);

        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setOnAction(e -> {
            String friendName = friendNameField.getText().trim();
            if (!friendName.isEmpty()) {
                addFriend(friendName);
            }
        });

        dialog.showAndWait();
    }

    private void addFriend(String friendName) {
        try {
            // 실제 친구 추가 로직 (서버 API 호출)
            // chatClient.sendMessage("/friendplus " + friendName) 에 해당하는 로직

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("친구 추가");
            alert.setContentText(friendName + "님을 친구로 추가했습니다.");
            alert.showAndWait();

            // 친구 목록 새로고침
            loadFriendData();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("친구 추가 실패");
            alert.setContentText("친구 추가에 실패했습니다: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadFriendData() {
        // 실제로는 서버에서 친구 목록을 받아와야 함
        // 임시 데이터
        friendData.put("소피아", true);   // online
        friendData.put("김서연", false);  // offline
        friendData.put("박민수", true);
        friendData.put("이지은", false);

        updateFriendList();
    }

    private void updateFriendList() {
        friendListContainer.getChildren().clear();

        for (Map.Entry<String, Boolean> entry : friendData.entrySet()) {
            String name = entry.getKey();
            Boolean isOnline = entry.getValue();

            HBox friendItem = createFriendItem(name, isOnline ? "online" : "offline");
            friendListContainer.getChildren().add(friendItem);
        }
    }

    private HBox createFriendItem(String name, String status) {
        HBox friendItem = new HBox();
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.setSpacing(10);
        friendItem.setPadding(new Insets(10));
        friendItem.setStyle("-fx-background-color: white;");

        // 프로필 이미지 (기존 RoundedPanel 대신 Label 사용)
        Label profileLabel = new Label();
        profileLabel.setPrefSize(60, 60);
        profileLabel.setStyle(
                "-fx-background-color: pink; " +
                        "-fx-background-radius: 30; " +
                        "-fx-border-radius: 30;"
        );

        // 친구 정보
        VBox friendInfo = new VBox();
        friendInfo.setSpacing(2);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: '맑은 고딕';");

        Label statusLabel = new Label(status);
        statusLabel.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-text-fill: " + (status.equals("online") ? "green" : "gray") + ";"
        );

        friendInfo.getChildren().addAll(nameLabel, statusLabel);

        // 호버 효과 (기존 MouseAdapter 기능)
        friendItem.setOnMouseEntered(e -> friendItem.setStyle("-fx-background-color: #f0f0f0;"));
        friendItem.setOnMouseExited(e -> friendItem.setStyle("-fx-background-color: white;"));
        friendItem.setOnMousePressed(e -> friendItem.setStyle("-fx-background-color: #e0e0e0;"));
        friendItem.setOnMouseReleased(e -> friendItem.setStyle("-fx-background-color: #f0f0f0;"));

        friendItem.getChildren().addAll(profileLabel, friendInfo);
        HBox.setHgrow(friendInfo, Priority.ALWAYS);

        return friendItem;
    }

    // 서버로부터 친구 데이터를 받아오는 메서드 (기존 sendfriendData 기능)
    public void updateFriendData(Map<String, Boolean> newFriendData) {
        this.friendData = newFriendData;
        updateFriendList();
    }
}
