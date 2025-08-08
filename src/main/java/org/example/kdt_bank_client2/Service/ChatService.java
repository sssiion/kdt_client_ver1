package org.example.kdt_bank_client2.Service;

import lombok.Setter;
import org.example.kdt_bank_client2.ChatSocketClient;
import org.example.kdt_bank_client2.Controller.ChatController;
import org.example.kdt_bank_client2.Controller.ChatRoomController;
import org.example.kdt_bank_client2.DTO.ChatMessageDto;
import org.example.kdt_bank_client2.DTO.UserDataDto;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

    public void enterRoom(String roomId, UserResponseDto user,
                         Consumer<ChatMessageDto> messageCallback,
                         Consumer<String> historyCallback,
                         Consumer<String> errorCallback) {
        try {

            // 1단계: REST API로 방 참가 처리
            //roomController.registerUserToRoom(roomId, user.getUserId());

            // 2단계: WebSocket 구독 설정
            socketClient.subscribeRoom(roomId, messageCallback);
            socketClient.subscribePersonal("history", historyCallback);
            socketClient.subscribePersonal("errors", errorCallback);

            // 입장 메시지 전송
            ChatMessageDto enterDto = createEnterMessage(roomId, user); // JOIN → ENTER
            socketClient.enterRoom(enterDto);

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
    //방 나가기 버튼에 있을 것
    public void exitChatRoom(String roomId, UserResponseDto user) {
        try {
            //ChatMessageDto leaveDto = createLeaveMessage(roomId, user);
            //socketClient.leaveRoom(leaveDto);
            //roomController.removeUserFromRoom(roomId, user.getUserId());
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

    // 🔥 DTO 생성 메서드들 수정 (서버 API에 맞춤)
    private ChatMessageDto createEnterMessage(String roomId, UserResponseDto user) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setUserId(user.getUserId());    // senderId → userId
        dto.setType("JOIN");                // messageType → type

        return dto;
    }

    private ChatMessageDto createChatMessage(String roomId, UserResponseDto user, String message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setUserId(user.getUserId());    // senderId → userId
        dto.setContent(message);            // message → content
        dto.setType("CHAT");                // messageType → type
        return dto;
    }

    private ChatMessageDto createLeaveMessage(String roomId, UserResponseDto user) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setUserId(user.getUserId());    // senderId → userId
        dto.setType("LEAVE");               // messageType → type
        return dto;
    }
}
