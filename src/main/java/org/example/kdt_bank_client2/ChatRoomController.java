package org.example.kdt_bank_client2;


import com.fasterxml.jackson.core.type.TypeReference;
import org.example.kdt_bank_client2.DTO.ApiResponse;
import org.example.kdt_bank_client2.DTO.ChatRoomResponseDto;
import org.example.kdt_bank_client2.DTO.createRoomDto;
import org.springframework.stereotype.Component;


import java.util.List;
@Component
public class ChatRoomController {

    public List<ChatRoomResponseDto> getAllRooms() throws Exception {
        ApiResponse<List<ChatRoomResponseDto>> response =
                ApiClient.get("/api/chatrooms", new TypeReference<ApiResponse<List<ChatRoomResponseDto>>>() {});

        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public ChatRoomResponseDto createRoom(String roomName) throws Exception {
        createRoomDto dto = new createRoomDto(roomName);
        ApiResponse<ChatRoomResponseDto> response =
                ApiClient.post("/api/chatrooms", dto, new TypeReference<ApiResponse<ChatRoomResponseDto>>() {});

        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public void joinRoom(String roomId, String userId) throws Exception {
        ApiResponse<String> response =
                ApiClient.post("/api/chatrooms/" + roomId + "/join?userId=" + userId, null,
                        new TypeReference<ApiResponse<String>>() {});

        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
    }

    public void leaveRoom(String roomId, String userId) throws Exception {
        ApiClient.delete("/api/chatrooms/" + roomId + "/leave?userId=" + userId);
    }

    public List<ChatRoomResponseDto> searchRooms(String keyword) throws Exception {
        ApiResponse<List<ChatRoomResponseDto>> response =
                ApiClient.get("/api/chatrooms/search?keyword=" + keyword,
                        new TypeReference<ApiResponse<List<ChatRoomResponseDto>>>() {});

        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }
}
