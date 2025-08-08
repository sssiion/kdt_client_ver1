package org.example.kdt_bank_client2.UI;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.DTO.UserDataDto;
import org.example.kdt_bank_client2.Service.ChatService;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.Service.UserService;
import org.example.kdt_bank_client2.Session.ChatRoomSession;
import org.example.kdt_bank_client2.Session.UserSession;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FriendListController {

    private final UserSession userSession; // UserResponseDto 대신
    private final ChatService chatService;
    private final UserService userService;
    private final ChatRoomSession chatRoomSession;
    private VBox friendListContainer;

    // 실시간 업데이트를 위한 인스턴스 변수
    //private Map<String, FriendInfo> friendData;
    private Map<String, FriendInfo> previousFriendData = new ConcurrentHashMap<>();
    private boolean isViewVisible = true;
    private final ConcurrentHashMap<String, FriendInfo> friendData = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private boolean isAutoRefreshEnabled = true;



    // 필터링을 위한 변수들
    private String currentSearchQuery = "";
    private Set<UserType> visibleUserTypes = EnumSet.allOf(UserType.class);

    private HBox mainLayoutContainer;
    private VBox inviteListContainer;
    private String selectedFriendName = null;

    public static class FriendInfo {
        public String name;
        public boolean isOnline;
        public UserType userType;
        public String lastSeen;
        public UserStatus status; // 상태메시지

        public FriendInfo(UserDataDto dto) {
            try{
                this.name = dto.getUserName();
                this.isOnline = dto.getIsOnline();
                this.userType = dto.getUserType();
                this.lastSeen = isOnline ? "방금 전" : "오프라인";
                this.status = dto.getStatus() != null ? dto.getStatus() : UserStatus.x;
                //this.status = dto.getStatus();

                System.out.println("FriendInfo 생성 성공: " + this.name);
            }catch(Exception e){
                System.out.println("Friendinfo 생성오류"+e.getMessage());
                e.printStackTrace();
                this.status = UserStatus.x;
            }
        }

    }

    // UserType enum (여기서 직접 정의하거나 import 사용)
    public enum UserType {
        BM("지점장"), DM("부지점장"), DH("팀장"),
        SC("수석컨설턴트"), AM("팀원"), BT("기타"), CSM("고객상담원");

        private final String displayName;

        UserType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    public enum UserStatus {
        out("출장"), meet("회의중"), con("고객상담중"), work("근무중"), away("자리비움"),play("휴식"),x("입력없음");
        private final String displayStatus;
        UserStatus(String displayStatus) {
            this.displayStatus = displayStatus;
        }
        public String getDisplayStatus(){
            return displayStatus;
        }

        public boolean isEmpty() {
            return "입력없음".equals(this.displayStatus);

        }
    }
    private void initializeScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            System.out.println("스케줄러 재초기화 중...");
            scheduler = Executors.newScheduledThreadPool(1);
            System.out.println("✅ 새로운 스케줄러 생성 완료");
        }
    }

    private void styleToggleButton(ToggleButton button) {
        button.setStyle(
                "-fx-background-color: #28a745; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 6 12 6 12;"
        );

        button.selectedProperty().addListener((obs, oldVal, newVal) -> {
            String color = newVal ? "#28a745" : "#dc3545";
            button.setStyle(
                    "-fx-background-color: " + color + "; " +
                            "-fx-background-radius: 20; " +
                            "-fx-border-radius: 20; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 12px; " +
                            "-fx-padding: 6 12 6 12;"
            );
        });
    }

    public VBox createFriendListView() {
        UserResponseDto currentUser = userSession.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("사용자가 로그인되지 않았습니다.");
        }

        VBox outerContainer = new VBox();
        outerContainer.setStyle("-fx-background-color: white;");
        outerContainer.setSpacing(10);

        // 상단 컨트롤 패널
        VBox controlPanel = createControlPanel();

        /// 메인 레이아웃을 HBox로 변경 (좌우 분할)
        mainLayoutContainer = new HBox();
        mainLayoutContainer.setSpacing(10);
        mainLayoutContainer.setPadding(new Insets(0, 10, 0, 10));

        // 왼쪽: 친구 목록 컨테이너
        VBox leftPanel = createLeftFriendPanel();

        // 오른쪽: 초대 목록 컨테이너 (처음엔 숨김)
        VBox rightPanel = createRightInvitePanel();
        rightPanel.setVisible(false);
        rightPanel.setManaged(false);

        mainLayoutContainer.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.NEVER);

        outerContainer.getChildren().addAll(controlPanel, mainLayoutContainer);
        VBox.setVgrow(mainLayoutContainer, Priority.ALWAYS);

        // 초기 데이터 로드
        fetchFriendDataFromServer();
        startRealTimeUpdate();

        return outerContainer;
    }
    /**
     * 왼쪽 친구 목록 패널 생성
     */
    private VBox createLeftFriendPanel() {
        VBox leftPanel = new VBox();
        leftPanel.setSpacing(5);
        leftPanel.setStyle("-fx-background-color: white;");

        friendListContainer = new VBox();
        friendListContainer.setSpacing(5);

        ScrollPane scrollPane = new ScrollPane(friendListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-width: 0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        leftPanel.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return leftPanel;
    }
    /**
     * 오른쪽 초대 목록 패널 생성
     */
    private VBox createRightInvitePanel() {
        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(300);
        rightPanel.setSpacing(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;"
        );

        // 헤더
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label headerLabel = new Label("초대 목록");
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button closeButton = new Button("✕");
        closeButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #6c757d; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 2 6;"
        );
        closeButton.setOnAction(e -> hideInvitePanel());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, closeButton);

        // 선택된 친구 정보
        VBox selectedFriendInfo = createSelectedFriendInfo();

        // 초대 목록 컨테이너
        inviteListContainer = new VBox();
        inviteListContainer.setSpacing(5);

        ScrollPane inviteScrollPane = new ScrollPane(inviteListContainer);
        inviteScrollPane.setFitToWidth(true);
        inviteScrollPane.setPrefHeight(400);
        inviteScrollPane.setStyle("-fx-background-color: white; -fx-border-width: 0;");
        inviteScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // 초대 버튼
        Button inviteButton = new Button("선택한 사용자들 초대");
        inviteButton.setStyle(
                "-fx-background-color: #007bff; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10 20; " +
                        "-fx-font-weight: bold;"
        );
        inviteButton.setOnAction(e -> handleInviteSelected());

        rightPanel.getChildren().addAll(
                header,
                selectedFriendInfo,
                new Label("초대할 사용자 선택:"),
                inviteScrollPane,
                inviteButton
        );

        return rightPanel;
    }/**
     * 선택된 친구 정보 표시
     */
    private VBox createSelectedFriendInfo() {
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(5);
        infoContainer.setPadding(new Insets(10));
        infoContainer.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        Label titleLabel = new Label("채팅할 상대:");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        Label nameLabel = new Label("선택된 친구 없음");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLabel.setId("selectedFriendName"); // ID로 나중에 찾을 수 있게

        infoContainer.getChildren().addAll(titleLabel, nameLabel);
        return infoContainer;
    }
    private void openChatWithFriend(String friendName) {
        System.out.println(friendName + "님과의 채팅방을 엽니다.");
        // 실제 채팅방 열기 로직
    }
    /**
     * 실시간 친구 상태 업데이트
     */
    public void updateFriendStatusRealTime(String friendName, boolean isOnline,UserStatus  status) {
        Platform.runLater(() -> {
            FriendInfo friend = friendData.get(friendName);
            if (friend != null) {
                friend.isOnline = isOnline;
                friend.status = status;
                friend.lastSeen = isOnline ? "방금 전" : "방금 오프라인";
                updateFriendListDisplay();
            }
        });
    }


    /**
     * 강제 새로고침
     */
    private void forceRefreshFriendData() {
        fetchFriendDataFromServer();
        showAlert("새로고침", "친구 목록을 업데이트했습니다.");
    }
    // 회원 추가
    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(userService);
        dialog.show();
    }


    /**
     * 자동 새로고침 토글
     */
    private void toggleAutoRefresh(boolean enabled) {
        isAutoRefreshEnabled = enabled;
        String message = enabled ? "자동 새로고침이 활성화되었습니다." : "자동 새로고침이 비활성화되었습니다.";
        showAlert("자동 새로고침", message);
    }
    private VBox createControlPanel() {
        VBox controlPanel = new VBox();
        controlPanel.setSpacing(10);

        // 검색바 (실시간 검색)
        TextField searchBar = new TextField();
        searchBar.setPromptText("친구 검색... (실시간)");
        searchBar.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-background-radius: 25; " +
                        "-fx-border-radius: 25; " +
                        "-fx-border-width: 0; " +
                        "-fx-padding: 10;"
        );

        // 실시간 검색 이벤트
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            currentSearchQuery = newValue.toLowerCase().trim();
            updateFriendListDisplay();
        });

        // 버튼들
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);


        Button refreshButton = createStyledButton("새로고침");
        refreshButton.setOnAction(e -> forceRefreshFriendData());

        ToggleButton autoRefreshToggle = new ToggleButton("자동새로고침");
        Button registerButton = createStyledButton("직원추가");
        registerButton.setOnAction(e -> showRegisterDialog());
        autoRefreshToggle.setSelected(isAutoRefreshEnabled);
        autoRefreshToggle.setOnAction(e -> toggleAutoRefresh(autoRefreshToggle.isSelected()));
        styleToggleButton(autoRefreshToggle);

        buttonContainer.getChildren().addAll( refreshButton, autoRefreshToggle,registerButton);

        controlPanel.getChildren().addAll(searchBar, buttonContainer);
        return controlPanel;
    }
    /**
     * 친구 데이터 업데이트 (UI 스레드에서 호출되어야 함)
     */
    private void updateFriendData(List<UserDataDto> statusList) {
        if (statusList == null) {
            System.out.println("statusList가 null입니다.");
            return;
        }

        UserResponseDto currentUser = userSession.getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUserId() : "";

        // 기존 데이터 클리어
        friendData.clear();

        // 새로운 데이터로 업데이트
        for (UserDataDto u : statusList) {
            if (u.getUserId() != null && u.getUserId().equals(currentUserId)) {
                System.out.println("자기 자신 제외: " + u.getUserName());
                continue; // 자기 자신은 건너뛰기
            }

            try {
                FriendInfo friendInfo = new FriendInfo(u);
                friendData.put(friendInfo.name, friendInfo);
                System.out.println("friendData에 추가 성공: " + friendInfo.name);
            } catch (Exception e) {
                System.err.println("FriendInfo 생성 실패: " + u.getUserName() + " - " + e.getMessage());
            }
        }

        System.out.println("친구 데이터 업데이트 완료. 총 " + friendData.size() + "명");
    }
    /**
     * 실제 부분 업데이트 수행
     */
    private void performPartialUpdate(Set<String> newFriends, Set<String> removedFriends, Set<String> changedFriends) {
        // 현재 표시된 TitledPane들을 찾아서 업데이트
        for (javafx.scene.Node node : friendListContainer.getChildren()) {
            if (node instanceof TitledPane) {
                TitledPane titledPane = (TitledPane) node;
                VBox friendsContainer = (VBox) titledPane.getContent();

                // 각 UserType 섹션별로 업데이트
                updateUserTypeSection(titledPane, friendsContainer, newFriends, removedFriends, changedFriends);
            }
        }
    }/**
     * HBox에서 친구 이름 추출
     */
    private String extractFriendNameFromHBox(HBox friendItem) {
        try {
            // HBox -> VBox(friendInfo) -> Label(nameLabel) 구조에서 이름 추출
            VBox friendInfo = (VBox) friendItem.getChildren().get(2); // statusDot, profile, friendInfo 순서
            Label nameLabel = (Label) friendInfo.getChildren().get(0);
            return nameLabel.getText();
        } catch (Exception e) {
            System.err.println("친구 이름 추출 실패: " + e.getMessage());
            return "";
        }
    }
    /**
     * UserType 섹션별 업데이트
     */
    private void updateUserTypeSection(TitledPane titledPane, VBox friendsContainer,
                                       Set<String> newFriends, Set<String> removedFriends, Set<String> changedFriends) {

        // 1. 삭제된 친구들 제거
        friendsContainer.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                String friendName = extractFriendNameFromHBox((HBox) node);
                return removedFriends.contains(friendName);
            }
            return false;
        });

        // 2. 변경된 친구들 업데이트
        for (javafx.scene.Node node : friendsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox friendItem = (HBox) node;
                String friendName = extractFriendNameFromHBox(friendItem);

                if (changedFriends.contains(friendName)) {
                    // 기존 항목을 새로운 것으로 교체
                    FriendInfo updatedFriend = friendData.get(friendName);
                    if (updatedFriend != null) {
                        HBox newFriendItem = createRealTimeFriendItem(updatedFriend);
                        int index = friendsContainer.getChildren().indexOf(friendItem);
                        friendsContainer.getChildren().set(index, newFriendItem);
                    }
                }
            }
        }

        // 3. 새로운 친구들 추가 (해당 UserType에 속하는 경우)
        UserType sectionType = extractUserTypeFromTitle(titledPane.getText());
        for (String friendName : newFriends) {
            FriendInfo newFriend = friendData.get(friendName);
            if (newFriend != null && newFriend.userType == sectionType) {
                HBox newFriendItem = createRealTimeFriendItem(newFriend);
                friendsContainer.getChildren().add(newFriendItem);
            }
        }

        // 4. 섹션 제목 업데이트 (온라인 수 / 전체 수)
        updateSectionTitle(titledPane, sectionType);
    }/**
     * 제목에서 UserType 추출
     */
    private UserType extractUserTypeFromTitle(String title) {
        try {
            String typeName = title.split("\\(")[0].trim(); // "지점장 (2/5)" -> "지점장"
            for (UserType type : UserType.values()) {
                if (type.getDisplayName().equals(typeName)) {
                    return type;
                }
            }
        } catch (Exception e) {
            System.err.println("UserType 추출 실패: " + e.getMessage());
        }
        return UserType.AM; // 기본값
    }
    /**
     * FriendInfo를 UserDataDto로 변환 (깊은 복사용)
     */
    private UserDataDto createUserDataDto(FriendInfo friendInfo) {
        UserDataDto dto = new UserDataDto();
        dto.setUserName(friendInfo.name);
        dto.setIsOnline(friendInfo.isOnline);
        dto.setUserType(friendInfo.userType);
        dto.setStatus(friendInfo.status);
        return dto;
    }
    /**
     * 이전 친구 데이터 백업 업데이트
     */
    private void updatePreviousFriendData() {
        previousFriendData.clear();
        for (Map.Entry<String, FriendInfo> entry : friendData.entrySet()) {
            FriendInfo original = entry.getValue();
            FriendInfo copy = new FriendInfo(createUserDataDto(original)); // 깊은 복사
            previousFriendData.put(entry.getKey(), copy);
        }
    }
    /**
     * 섹션 제목 업데이트
     */
    private void updateSectionTitle(TitledPane titledPane, UserType userType) {
        List<FriendInfo> friends = friendData.values().stream()
                .filter(f -> f.userType == userType)
                .collect(Collectors.toList());

        long onlineCount = friends.stream().mapToLong(f -> f.isOnline ? 1 : 0).sum();
        String newTitle = String.format("%s (%d/%d) ⚡",
                userType.getDisplayName(), onlineCount, friends.size());

        titledPane.setText(newTitle);
    }

    /**
     * 두 FriendInfo 객체가 같은지 비교
     */
    private boolean isFriendInfoEqual(FriendInfo current, FriendInfo previous) {
        if (current == null && previous == null) return true;
        if (current == null || previous == null) return false;

        return current.isOnline == previous.isOnline &&
                Objects.equals(current.status, previous.status) &&
                Objects.equals(current.userType, previous.userType) &&
                Objects.equals(current.name, previous.name);
    }
    // 나머지 메서드들은 동일...
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
    /**
     * 변경된 항목만 업데이트하는 최적화된 메서드
     */
    private void updateOnlyChangedItems() {
        try {
            // 1. 새로 추가된 친구들 찾기
            Set<String> newFriends = new HashSet<>(friendData.keySet());
            newFriends.removeAll(previousFriendData.keySet());

            // 2. 삭제된 친구들 찾기
            Set<String> removedFriends = new HashSet<>(previousFriendData.keySet());
            removedFriends.removeAll(friendData.keySet());

            // 3. 변경된 친구들 찾기 (상태, 온라인 여부 변경)
            Set<String> changedFriends = new HashSet<>();
            for (String friendName : friendData.keySet()) {
                FriendInfo current = friendData.get(friendName);
                FriendInfo previous = previousFriendData.get(friendName);

                if (previous != null && !isFriendInfoEqual(current, previous)) {
                    changedFriends.add(friendName);
                }
            }

            // 4. 변경사항이 많으면 전체 업데이트, 적으면 부분 업데이트
            int totalChanges = newFriends.size() + removedFriends.size() + changedFriends.size();
            int totalFriends = friendData.size();

            if (totalChanges > totalFriends * 0.3) { // 30% 이상 변경시 전체 업데이트
                System.out.println("📊 변경사항이 많아 전체 업데이트 수행");
                updateFriendListDisplay();
            } else {
                System.out.println("⚡ 부분 업데이트 수행 - 변경: " + totalChanges + "개");
                performPartialUpdate(newFriends, removedFriends, changedFriends);
            }

            // 5. 이전 데이터 백업 업데이트
            updatePreviousFriendData();

        } catch (Exception e) {
            System.err.println("부분 업데이트 실패, 전체 업데이트로 대체: " + e.getMessage());
            updateFriendListDisplay(); // 실패시 전체 업데이트
        }
    }

    /**
     * 서버에서 친구 데이터 실시간 가져오기 (스레드 안전 버전)
     */
    private void fetchFriendDataFromServer() {
        try {

            if (!userSession.isLoggedIn()) {
                System.out.println("로그인되지 않은 상태 - 데이터 요청 중단");
                return;
            }

            // 🔥 개선: 백그라운드에서 데이터 가져오기
            CompletableFuture.supplyAsync(() -> {
                try {
                    return userService.getStatus();
                } catch (Exception e) {
                    System.err.println("서버에서 친구 데이터를 가져오는 중 오류: " + e.getMessage());
                    return Collections.<UserDataDto>emptyList();
                }
            }).thenAccept(statusList -> {
                // 🔥 개선: UI 업데이트는 반드시 JavaFX Application Thread에서
                Platform.runLater(() -> {
                    updateFriendData(statusList);        // 새로 만든 메서드 호출
                    updateFriendListDisplay();
                });
            }).exceptionally(throwable -> {
                System.err.println("친구 데이터 가져오기 실패: " + throwable.getMessage());
                return null;
            });

        } catch (Exception e) {
            System.err.println("fetchFriendDataFromServer 실행 중 오류: " + e.getMessage());
        }
    }
    /**
     * 실시간 업데이트 시작
     */
    private void startRealTimeUpdate() {
        initializeScheduler();
        scheduler.scheduleAtFixedRate(() -> {
            if (isAutoRefreshEnabled) {
                Platform.runLater(() -> {
                    try {
                        fetchFriendDataFromServer(); // 서버에서 최신 데이터 가져오기
                    } catch (Exception e) {
                        System.err.println("실시간 업데이트 중 오류: " + e.getMessage());
                    }
                });
            }
        }, 0, 60, TimeUnit.SECONDS); // 3초마다 업데이트
    }


    private TitledPane createUserTypeSection(UserType userType, List<FriendInfo> friends) {
        long onlineCount = friends.stream().mapToLong(f -> f.isOnline ? 1 : 0).sum();

        String title = String.format("%s (%d/%d) 🔄",
                userType.getDisplayName(), onlineCount, friends.size());

        VBox friendsContainer = new VBox();
        friendsContainer.setSpacing(2);

        // 온라인 상태별로 정렬
        friends.sort((f1, f2) -> {
            if (f1.isOnline != f2.isOnline) {
                return Boolean.compare(f2.isOnline, f1.isOnline);
            }
            return f1.name.compareTo(f2.name);
        });

        for (FriendInfo friend : friends) {
            HBox friendItem = createRealTimeFriendItem(friend);
            friendsContainer.getChildren().add(friendItem);
        }

        TitledPane titledPane = new TitledPane(title, friendsContainer);
        titledPane.setExpanded(true); // 실시간 업데이트 확인을 위해 펼친 상태

        titledPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        return titledPane;
    }
    /**
     * 실시간 UI 업데이트
     */
    private void updateFriendListDisplay() {
        Platform.runLater(() -> {
            if (!isViewVisible) return;
            System.out.println("friendData 크기: " + friendData.size());
            friendListContainer.getChildren().clear();
            // 검색 필터링 전 디버깅
            System.out.println("필터링 전 데이터:");
            friendData.forEach((key, value) -> {
                System.out.println("  " + key + " -> " + value.userType);
            });

            // 검색 필터링
            Map<String, FriendInfo> filteredFriends = friendData.entrySet().stream()
                    .filter(entry -> {
                        FriendInfo friend = entry.getValue();
                        boolean matchesSearch = currentSearchQuery.isEmpty() ||
                                friend.name.toLowerCase().contains(currentSearchQuery);
                        boolean matchesType = visibleUserTypes.contains(friend.userType);
                        System.out.println("필터링 체크 - " + friend.name +
                                ": 검색매치=" + matchesSearch +
                                ", 타입매치=" + matchesType);
                        return matchesSearch && matchesType;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // UserType별로 그룹화
            Map<UserType, List<FriendInfo>> groupedFriends = filteredFriends.values()
                    .stream()
                    .collect(Collectors.groupingBy(friend -> friend.userType));

            // 각 UserType별로 TitledPane 생성
            for (UserType userType : UserType.values()) {
                List<FriendInfo> friends = groupedFriends.getOrDefault(userType, new ArrayList<>());

                if (!friends.isEmpty()) {
                    TitledPane titledPane = createUserTypeSection(userType, friends);
                    friendListContainer.getChildren().add(titledPane);
                }
            }
        });
    }



    private HBox createRealTimeFriendItem(FriendInfo friend) {
        HBox friendItem = new HBox();
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.setSpacing(10);
        friendItem.setPadding(new Insets(8, 10, 8, 20));

        // 온라인 상태에 따른 배경색 변경
        String backgroundColor = friend.isOnline ? "#f8fff8" : "white";
        friendItem.setStyle("-fx-background-color: " + backgroundColor + ";");

        // 실시간 상태 표시
        Label statusDot = new Label("●");
        String dotColor = friend.isOnline ? "#4CAF50" : "#BDBDBD";
        statusDot.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 12px;");

        Label profileLabel = new Label();
        profileLabel.setPrefSize(45, 45);
        String profileColor = friend.isOnline ? "#E8F5E8" : "#F5F5F5";
        profileLabel.setStyle(
                "-fx-background-color: " + profileColor + "; " +
                        "-fx-background-radius: 22.5; " +
                        "-fx-border-radius: 22.5; " +
                        "-fx-border-color: " + dotColor + "; " +
                        "-fx-border-width: 2px;"
        );

        VBox friendInfo = new VBox();
        friendInfo.setSpacing(1);

        Label nameLabel = new Label(friend.name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: '맑은 고딕';");

        // 상태메시지 또는 마지막 접속 시간
        String statusText = friend.status.isEmpty() ?
                (friend.isOnline ? "온라인" : friend.lastSeen) : friend.status.getDisplayStatus();

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle(
                "-fx-font-size: 11px; " +
                        "-fx-text-fill: " + (friend.isOnline ? "#4CAF50" : "#BDBDBD") + ";"
        );

        friendInfo.getChildren().addAll(nameLabel, statusLabel);

        // 마우스 이벤트
        friendItem.setOnMouseEntered(e -> {
            String hoverColor = friend.isOnline ? "#f0fff0" : "#f8f9fa";
            friendItem.setStyle("-fx-background-color: " + hoverColor + ";");
        });
        friendItem.setOnMouseExited(e -> {
            friendItem.setStyle("-fx-background-color: " + backgroundColor + ";");
        });

        // 더블클릭으로 채팅방 열기
        friendItem.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                showInvitePanelForFriend(friend.name);
            }
        });

        friendItem.getChildren().addAll(statusDot, profileLabel, friendInfo);
        HBox.setHgrow(friendInfo, Priority.ALWAYS);

        return friendItem;
    }/**
     * 친구 선택 시 초대 패널 표시
     */
    private void showInvitePanelForFriend(String friendName) {
        this.selectedFriendName = friendName;

        // 오른쪽 패널 표시
        VBox rightPanel = (VBox) mainLayoutContainer.getChildren().get(1);
        rightPanel.setVisible(true);
        rightPanel.setManaged(true);

        // 선택된 친구 이름 업데이트
        updateSelectedFriendDisplay(friendName);

        // 초대 가능한 사용자 목록 로드
        loadInvitableUsers(friendName);

        System.out.println("✅ " + friendName + "님과의 채팅을 위한 초대 패널 표시");
    }/**
     * 선택된 친구 표시 업데이트
     */
    private void updateSelectedFriendDisplay(String friendName) {
        VBox rightPanel = (VBox) mainLayoutContainer.getChildren().get(1);
        VBox selectedFriendInfo = (VBox) rightPanel.getChildren().get(1);

        for (javafx.scene.Node node : selectedFriendInfo.getChildren()) {
            if (node.getId() != null && node.getId().equals("selectedFriendName")) {
                ((Label) node).setText(friendName);
                break;
            }
        }
    }/**
     * 초대 가능한 사용자 목록 로드
     */
    private void loadInvitableUsers(String excludeFriendName) {
        inviteListContainer.getChildren().clear();

        try {
            List<UserDataDto> allUsers = userService.getStatus();
            UserResponseDto currentUser = userSession.getCurrentUser();
            String currentUserId = currentUser != null ? currentUser.getUserId() : "";

            for (UserDataDto user : allUsers) {
                // 현재 사용자와 선택된 친구는 제외
                if (user.getUserId() != null &&
                        !user.getUserId().equals(currentUserId) &&
                        !user.getUserName().equals(excludeFriendName)) {

                    HBox userItem = createInvitableUserItem(user);
                    inviteListContainer.getChildren().add(userItem);
                }
            }

            System.out.println("✅ 초대 가능한 사용자 " + inviteListContainer.getChildren().size() + "명 로드");

        } catch (Exception e) {
            System.err.println("❌ 초대 가능한 사용자 로드 실패: " + e.getMessage());

            Label errorLabel = new Label("사용자 목록을 불러올 수 없습니다.");
            errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px; -fx-padding: 10;");
            inviteListContainer.getChildren().add(errorLabel);
        }
    }/**
     * 초대 가능한 사용자 아이템 생성
     */
    private HBox createInvitableUserItem(UserDataDto user) {
        HBox userItem = new HBox();
        userItem.setAlignment(Pos.CENTER_LEFT);
        userItem.setSpacing(8);
        userItem.setPadding(new Insets(8));
        userItem.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        // 체크박스
        CheckBox checkBox = new CheckBox();
        checkBox.setUserData(user); // 사용자 데이터 저장

        // 상태 표시
        Label statusDot = new Label("●");
        String dotColor = user.getIsOnline() ? "#4CAF50" : "#BDBDBD";
        statusDot.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 10px;");

        // 사용자 정보
        VBox userInfo = new VBox();
        userInfo.setSpacing(2);

        Label nameLabel = new Label(user.getUserName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        String statusText = user.getStatus() != null && !user.getStatus().isEmpty() ?
                user.getStatus().getDisplayStatus() : (user.getIsOnline() ? "온라인" : "오프라인");

        Label statusLabel = new Label(statusText + " • " + user.getUserType().getDisplayName());
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6c757d;");

        userInfo.getChildren().addAll(nameLabel, statusLabel);

        // 호버 효과
        userItem.setOnMouseEntered(e -> {
            userItem.setStyle(
                    "-fx-background-color: #f8f9fa; " +
                            "-fx-border-color: #007bff; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px;"
            );
        });

        userItem.setOnMouseExited(e -> {
            userItem.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #dee2e6; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px;"
            );
        });

        // 클릭 시 체크박스 토글
        userItem.setOnMouseClicked(e -> checkBox.setSelected(!checkBox.isSelected()));

        userItem.getChildren().addAll(checkBox, statusDot, userInfo);
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        return userItem;
    }/**
     * 선택된 사용자들 초대 처리
     */
    private void handleInviteSelected() {
        List<UserDataDto> selectedUsers = new ArrayList<>();

        // 선택된 사용자들 찾기
        for (javafx.scene.Node node : inviteListContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox userItem = (HBox) node;
                CheckBox checkBox = (CheckBox) userItem.getChildren().get(0);

                if (checkBox.isSelected()) {
                    UserDataDto user = (UserDataDto) checkBox.getUserData();
                    selectedUsers.add(user);
                }
            }
        }

        if (selectedUsers.isEmpty()) {
            showAlert("알림", "초대할 사용자를 선택해주세요.");
            return;
        }

        // 초대 처리 로직
        StringBuilder inviteList = new StringBuilder();
        inviteList.append(selectedFriendName).append("님과 함께 초대할 사용자:\n\n");

        for (UserDataDto user : selectedUsers) {
            inviteList.append("• ").append(user.getUserName())
                    .append(" (").append(user.getUserType().getDisplayName()).append(")\n");
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("초대 확인");
        confirmAlert.setHeaderText("다음 사용자들을 채팅방에 초대하시겠습니까?");
        confirmAlert.setContentText(inviteList.toString());

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 실제 채팅방 생성 및 초대 로직
                createChatRoomWithInvites(selectedFriendName, selectedUsers);
            }
        });
    }

    /**
     * 초대된 사용자들과 함께 채팅방 생성
     */
    private void createChatRoomWithInvites(String friendName, List<UserDataDto> invitedUsers) {
        try {
            StringBuilder roomName = new StringBuilder();
            roomName.append(friendName);

            for (UserDataDto user : invitedUsers) {
                roomName.append(", ").append(user.getUserName());
            }

            // TODO: 실제 채팅방 생성 API 호출
            System.out.println("🔄 채팅방 생성 중: " + roomName.toString());

            showAlert("성공", "채팅방이 생성되었습니다!\n참여자: " + roomName.toString());
            hideInvitePanel();

        } catch (Exception e) {
            System.err.println("❌ 채팅방 생성 실패: " + e.getMessage());
            showAlert("오류", "채팅방 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 초대 패널 숨기기
     */
    private void hideInvitePanel() {
        VBox rightPanel = (VBox) mainLayoutContainer.getChildren().get(1);
        rightPanel.setVisible(false);
        rightPanel.setManaged(false);

        this.selectedFriendName = null;
        inviteListContainer.getChildren().clear();

        System.out.println("✅ 초대 패널 숨김");
    }


    /**
     * 컴포넌트 종료 시 스케줄러 정리
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("스케줄러 강제 종료");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 직책별 사용자 목록을 보여주는 다이얼로그 (FriendListController 기반)
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showUserListDialog() {
        if (this.userSession == null) {
            System.err.println("❌ userSession이 null입니다!");
            return;
        }
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("직책별 직원 목록");
        dialog.setHeaderText("현재 등록된 직원들을 직책별로 확인할 수 있습니다");

        // 다이얼로그 크기 설정
        dialog.getDialogPane().setPrefSize(600, 700);
        dialog.setResizable(true);

        // 버튼 설정
        ButtonType closeButtonType = new ButtonType("닫기", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType refreshButtonType = new ButtonType("새로고침", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(refreshButtonType, closeButtonType);

        try {
            // 메인 컨테이너 생성
            VBox mainContainer = new VBox();
            mainContainer.setSpacing(10);
            mainContainer.setPadding(new Insets(15));
            mainContainer.setStyle("-fx-background-color: white;");

            // 상단 헤더 (현재 사용자 정보, 시간 등)
            VBox headerInfo = createDialogHeaderInfo();

            // 검색바 추가
            TextField searchField = createSearchField();

            // 사용자 목록 컨테이너
            VBox userListContainer = createDialogUserList();

            ScrollPane scrollPane = new ScrollPane(userListContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: white; -fx-border-width: 0;");
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            // 검색 기능 연결
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterAndUpdateDialogList(userListContainer, newValue.toLowerCase().trim());
            });

            mainContainer.getChildren().addAll(headerInfo, searchField, scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            dialog.getDialogPane().setContent(mainContainer);
            dialog.getDialogPane().setStyle("-fx-background-color: white;");

            // 새로고침 버튼 이벤트 처리
            Button refreshButton = (Button) dialog.getDialogPane().lookupButton(refreshButtonType);
            refreshButton.setOnAction(e -> {
                userListContainer.getChildren().clear();
                VBox newList = createDialogUserList();
                userListContainer.getChildren().addAll(newList.getChildren());
                showAlert("새로고침", "직원 목록을 업데이트했습니다.");
            });

            System.out.println("✅ 직원 목록 다이얼로그 생성 완료");

        } catch (Exception e) {
            System.err.println("❌ 다이얼로그 생성 중 오류: " + e.getMessage());
            showErrorDialog("다이얼로그 오류", "직원 목록 다이얼로그 생성 중 오류가 발생했습니다.");
            return;
        }

        dialog.showAndWait();
    }

    /**
     * 다이얼로그용 헤더 정보 생성
     */
    private VBox createDialogHeaderInfo() {
        VBox header = new VBox();
        header.setSpacing(5);
        header.setPadding(new Insets(0, 0, 10, 0));
        header.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        try {
            UserResponseDto currentUser = userSession.getCurrentUser();
            String currentUserName = currentUser != null ? currentUser.getUserName() : "알 수 없음";

            Label timeLabel = new Label("📅 조회 시간: " +
                    java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

            Label userLabel = new Label("👤 조회자: " + currentUserName);
            userLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

            header.getChildren().addAll(timeLabel, userLabel);

        } catch (Exception e) {
            System.err.println("헤더 생성 중 오류: " + e.getMessage());
        }

        return header;
    }

    /**
     * 다이얼로그용 검색 필드 생성
     */
    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("직원 이름으로 검색...");
        searchField.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-padding: 8 15;"
        );
        return searchField;
    }

    /**
     * 다이얼로그용 사용자 목록 생성
     */
    private VBox createDialogUserList() {
        VBox container = new VBox();
        container.setSpacing(8);

        try {
            // 서버에서 데이터 가져오기
            List<UserDataDto> userList = userService.getStatus();

            if (userList == null || userList.isEmpty()) {
                Label noDataLabel = new Label("📋 등록된 직원이 없습니다.");
                noDataLabel.setStyle(
                        "-fx-font-size: 14px; " +
                                "-fx-text-fill: #6c757d; " +
                                "-fx-padding: 30; " +
                                "-fx-alignment: center;"
                );
                container.getChildren().add(noDataLabel);
                return container;
            }

            // 현재 사용자 제외
            UserResponseDto currentUser = userSession.getCurrentUser();
            String currentUserId = currentUser != null ? currentUser.getUserId() : "";

            // FriendInfo로 변환
            List<FriendInfo> friendList = new ArrayList<>();
            for (UserDataDto user : userList) {
                if (user.getUserId() != null && !user.getUserId().equals(currentUserId)) {
                    try {
                        FriendInfo friendInfo = new FriendInfo(user);
                        friendList.add(friendInfo);
                    } catch (Exception e) {
                        System.err.println("FriendInfo 생성 실패: " + user.getUserName());
                    }
                }
            }

            // 통계 정보 추가
            addStatisticsInfo(container, friendList);

            // UserType별로 그룹화
            Map<UserType, List<FriendInfo>> groupedByPosition = friendList.stream()
                    .collect(Collectors.groupingBy(friend -> friend.userType));

            // 각 직책별 섹션 생성
            for (UserType userType : UserType.values()) {
                List<FriendInfo> friends = groupedByPosition.getOrDefault(userType, new ArrayList<>());

                if (!friends.isEmpty()) {
                    TitledPane positionSection = createDialogPositionSection(userType, friends);
                    container.getChildren().add(positionSection);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ 다이얼로그용 사용자 목록 생성 중 오류: " + e.getMessage());
            showErrorLabel(container, "데이터 로드 중 오류가 발생했습니다.");
        }

        return container;
    }

    /**
     * 통계 정보 추가
     */
    private void addStatisticsInfo(VBox container, List<FriendInfo> friendList) {
        int totalUsers = friendList.size();
        long totalOnline = friendList.stream().mapToLong(f -> f.isOnline ? 1 : 0).sum();

        HBox statsContainer = new HBox();
        statsContainer.setSpacing(20);
        statsContainer.setAlignment(Pos.CENTER_LEFT);
        statsContainer.setPadding(new Insets(8, 12, 8, 12));
        statsContainer.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10;"
        );

        Label totalLabel = new Label("👥 전체: " + totalUsers + "명");
        Label onlineLabel = new Label("🟢 온라인: " + totalOnline + "명");
        Label offlineLabel = new Label("⚪ 오프라인: " + (totalUsers - totalOnline) + "명");

        String labelStyle = "-fx-font-size: 12px; -fx-font-weight: bold;";
        totalLabel.setStyle(labelStyle + " -fx-text-fill: #495057;");
        onlineLabel.setStyle(labelStyle + " -fx-text-fill: #28a745;");
        offlineLabel.setStyle(labelStyle + " -fx-text-fill: #6c757d;");

        statsContainer.getChildren().addAll(totalLabel, onlineLabel, offlineLabel);
        container.getChildren().add(statsContainer);
    }

    /**
     * 다이얼로그용 직책별 섹션 생성
     */
    private TitledPane createDialogPositionSection(UserType userType, List<FriendInfo> friends) {
        long onlineCount = friends.stream().mapToLong(f -> f.isOnline ? 1 : 0).sum();

        String onlineIcon = onlineCount > 0 ? "🟢" : "⚪";
        String title = String.format("%s %s (%d/%d)",
                onlineIcon, userType.getDisplayName(), onlineCount, friends.size());

        VBox friendsContainer = new VBox();
        friendsContainer.setSpacing(4);
        friendsContainer.setPadding(new Insets(8));

        // 정렬 (온라인 우선, 이름순)
        friends.sort((f1, f2) -> {
            if (f1.isOnline != f2.isOnline) {
                return Boolean.compare(f2.isOnline, f1.isOnline);
            }
            return f1.name.compareTo(f2.name);
        });

        for (FriendInfo friend : friends) {
            HBox friendItem = createDialogFriendItem(friend);
            friendsContainer.getChildren().add(friendItem);
        }

        TitledPane titledPane = new TitledPane(title, friendsContainer);
        titledPane.setExpanded(true);
        titledPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        );

        return titledPane;
    }

    /**
     * 다이얼로그용 친구 아이템 생성 (기존 createRealTimeFriendItem 기반)
     */
    private HBox createDialogFriendItem(FriendInfo friend) {
        HBox friendItem = new HBox();
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.setSpacing(10);
        friendItem.setPadding(new Insets(6, 10, 6, 10));

        String backgroundColor = friend.isOnline ? "#e8f5e9" : "#f8f9fa";
        friendItem.setStyle(
                "-fx-background-color: " + backgroundColor + "; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8;"
        );

        // 상태 표시점
        Label statusDot = new Label("●");
        String dotColor = friend.isOnline ? "#4CAF50" : "#BDBDBD";
        statusDot.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 10px;");

        // 프로필 (작게)
        Label profileLabel = new Label();
        profileLabel.setPrefSize(35, 35);
        String profileColor = friend.isOnline ? "#c8e6c9" : "#e9ecef";
        profileLabel.setStyle(
                "-fx-background-color: " + profileColor + "; " +
                        "-fx-background-radius: 17.5; " +
                        "-fx-border-radius: 17.5; " +
                        "-fx-border-color: " + dotColor + "; " +
                        "-fx-border-width: 1px;"
        );

        // 사용자 정보
        VBox friendInfo = new VBox();
        friendInfo.setSpacing(2);

        Label nameLabel = new Label(friend.name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        String statusText = friend.status.isEmpty() ?
                (friend.isOnline ? "온라인" : "오프라인") : friend.status.getDisplayStatus();

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle(
                "-fx-font-size: 10px; " +
                        "-fx-text-fill: " + (friend.isOnline ? "#4CAF50" : "#6c757d") + ";"
        );

        friendInfo.getChildren().addAll(nameLabel, statusLabel);

        // 마우스 호버 효과
        friendItem.setOnMouseEntered(e -> {
            String hoverColor = friend.isOnline ? "#d4edda" : "#e9ecef";
            friendItem.setStyle(
                    "-fx-background-color: " + hoverColor + "; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-radius: 8;"
            );
        });

        friendItem.setOnMouseExited(e -> {
            friendItem.setStyle(
                    "-fx-background-color: " + backgroundColor + "; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-radius: 8;"
            );
        });

        friendItem.getChildren().addAll(statusDot, profileLabel, friendInfo);
        HBox.setHgrow(friendInfo, Priority.ALWAYS);

        return friendItem;
    }

    /**
     * 검색 필터링 및 목록 업데이트
     */
    private void filterAndUpdateDialogList(VBox container, String searchQuery) {
        try {
            for (javafx.scene.Node node : container.getChildren()) {
                if (node instanceof TitledPane) {
                    TitledPane titledPane = (TitledPane) node;
                    VBox friendsContainer = (VBox) titledPane.getContent();

                    boolean hasVisibleItems = false;

                    for (javafx.scene.Node friendNode : friendsContainer.getChildren()) {
                        if (friendNode instanceof HBox) {
                            HBox friendItem = (HBox) friendNode;
                            String friendName = extractFriendNameFromHBox(friendItem);

                            boolean matches = searchQuery.isEmpty() ||
                                    friendName.toLowerCase().contains(searchQuery);

                            friendNode.setVisible(matches);
                            friendNode.setManaged(matches);

                            if (matches) hasVisibleItems = true;
                        }
                    }

                    // 섹션 자체의 가시성 설정
                    titledPane.setVisible(hasVisibleItems);
                    titledPane.setManaged(hasVisibleItems);
                }
            }
        } catch (Exception e) {
            System.err.println("검색 필터링 중 오류: " + e.getMessage());
        }
    }

    /**
     * 오류 레이블 표시
     */
    private void showErrorLabel(VBox container, String message) {
        Label errorLabel = new Label("⚠️ " + message);
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc3545; -fx-padding: 20;");
        container.getChildren().add(errorLabel);
    }

    /**
     * 오류 다이얼로그 표시
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

