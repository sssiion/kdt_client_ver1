package org.example.kdt_bank_client2;

import lombok.Setter;
import org.example.kdt_bank_client2.DTO.ChatMessageDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
@Component
@Setter
public class ChatService {
    private final ChatSocketClient socketClient;
    private final ChatRoomController roomController;

    public ChatService(ChatSocketClient socketClient, ChatRoomController roomController) {
        this.socketClient = socketClient;
        this.roomController = roomController;
    }

    public void joinRoom(String roomId, UserResponseDto user,
                         Consumer<ChatMessageDto> messageCallback,
                         Consumer<String> historyCallback,
                         Consumer<String> errorCallback) {
        try {
            // 1단계: REST API로 방 참가 처리
            roomController.joinRoom(roomId, user.getUserId());

            // 2단계: WebSocket 구독 설정
            socketClient.subscribeRoom(roomId, messageCallback);
            socketClient.subscribePersonal("history", historyCallback);
            socketClient.subscribePersonal("errors", errorCallback);

            // 3단계: 입장 메시지 전송
            ChatMessageDto joinDto = createJoinMessage(roomId, user);
            socketClient.enterRoom(joinDto);

            System.out.println("✅ 방 참가 완료: " + roomId);

        } catch (Exception e) {
            System.err.println("❌ 방 참가 실패: " + e.getMessage());
            errorCallback.accept("방 참가 실패: " + e.getMessage());
        }
    }

    public void sendMessage(String roomId, UserResponseDto user, String message) {
        if (message == null || message.trim().isEmpty()) {
            System.err.println("빈 메시지는 전송할 수 없습니다.");
            return;
        }

        ChatMessageDto dto = createChatMessage(roomId, user, message);
        socketClient.sendMessage(dto);
    }

    public void leaveRoom(String roomId, UserResponseDto user) {
        try {
            ChatMessageDto leaveDto = createLeaveMessage(roomId, user);
            socketClient.leaveRoom(leaveDto);
            roomController.leaveRoom(roomId, user.getUserId());
            System.out.println("✅ 방 나가기 완료: " + roomId);
        } catch (Exception e) {
            System.err.println("❌ 방 나가기 실패: " + e.getMessage());
        }
    }

    public void connectWebSocket(String userId, Runnable onConnect, Consumer<String> onError) {
        socketClient.connect(userId, onConnect, onError);
    }

    public void disconnectWebSocket() {
        socketClient.disconnect();
    }

    public boolean isConnected() {
        return socketClient.isConnected();
    }

    // DTO 생성 헬퍼 메서드들
    private ChatMessageDto createJoinMessage(String roomId, UserResponseDto user) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setSenderId(user.getUserId());
        dto.setSenderName(user.getUserName());
        dto.setMessageType("JOIN");
        return dto;
    }

    private ChatMessageDto createChatMessage(String roomId, UserResponseDto user, String message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setSenderId(user.getUserId());
        dto.setSenderName(user.getUserName());
        dto.setMessage(message);
        dto.setMessageType("CHAT");
        return dto;
    }

    private ChatMessageDto createLeaveMessage(String roomId, UserResponseDto user) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setSenderId(user.getUserId());
        dto.setSenderName(user.getUserName());
        dto.setMessageType("LEAVE");
        return dto;
    }
}
