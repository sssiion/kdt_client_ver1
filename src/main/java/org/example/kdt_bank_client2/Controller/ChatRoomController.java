package org.example.kdt_bank_client2.Controller;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.DTO.*;
import org.example.kdt_bank_client2.DTO.ApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@Component
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private UserResponseDto currentUser;
    private ChatRoomResponseDto chatRoom;
    private final ApiClient apiClient;

    public List<ChatRoomResponseDto> getAllRooms() throws Exception {
        ApiResponse<List<ChatRoomResponseDto>> response =
                apiClient.get("/api/chatrooms", new TypeReference<ApiResponse<List<ChatRoomResponseDto>>>() {});

        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }
    
    // 특정 사용자 참여중인 모든방
    public List<ChatRoomResponseDto> getUserJoinRooms(String userId) throws Exception{
        ApiResponse<List<ChatRoomResponseDto>> response =
                apiClient.get("/api/users/" + userId + "/rooms", new TypeReference<ApiResponse<List<ChatRoomResponseDto>>>() {});
        System.out.println(response);
        if (response.isSuccess()) {
            return response.getData();

        }else{
            throw new RuntimeException(response.getMessage());
        }
    }


    public ApiResponse<ChatRoomResponseDto> createChatRoom(String roomName,String userId) throws Exception {
        CreateRoomDto dto = new CreateRoomDto(roomName);
        System.out.println(dto);
        String url = "/api/chatrooms?userId=" + userId;
        ApiResponse<ChatRoomResponseDto> response =apiClient.post(url, dto, new TypeReference<ApiResponse<ChatRoomResponseDto>>() {});
        System.out.println(response +"들어감");
        return response;

    }

    public void registerUserToRoom(String roomId, String userId) throws Exception {
        ApiResponse<String> response =
                apiClient.post("/api/chatrooms/" + roomId + "/join?userId=" + userId, null,
                        new TypeReference<ApiResponse<String>>() {});

        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
    }

    public void removeUserFromRoom(String roomId, String userId) throws Exception {
        apiClient.delete("/api/chatrooms/" + roomId + "/leave?userId=" + userId);

    }

    public List<ChatRoomResponseDto> searchRooms(String keyword) throws Exception {
        ApiResponse<List<ChatRoomResponseDto>> response =
                apiClient.get("/api/chatrooms/search?keyword=" + keyword,
                        new TypeReference<ApiResponse<List<ChatRoomResponseDto>>>() {});

        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }
    public List<ChatMessageDto> getMessage(String roomId) throws Exception {
        ApiResponse<List<ChatMessageDto>> response =
                apiClient.get("/api/Message/" + roomId, new TypeReference<ApiResponse<List<ChatMessageDto>>>() {});
        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }


    // 🔥 새로 추가: 특정 채팅방 조회
    public ChatRoomResponseDto getRoom(String roomId) throws Exception {
        ApiResponse<ChatRoomResponseDto> response =
                apiClient.get("/api/chatrooms/" + roomId, new TypeReference<ApiResponse<ChatRoomResponseDto>>() {});

        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }
}
