package org.example.kdt_bank_client2.Session;

import org.example.kdt_bank_client2.DTO.ChatRoomResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatRoomSession {
    private String roomId;
    private String roomName;
    private Integer userCount;
    private String createdAt;
    private boolean isInRoom = false;

    public void setCurrentRoom(String roomId, String roomName, Integer userCount, String createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.userCount = userCount;
        this.createdAt = createdAt;
        this.isInRoom = true;
    }

    // DTO에서 정보 복사하는 편의 메서드
    public void setCurrentRoom(ChatRoomResponseDto room) {
        this.roomId = room.getRoomId();
        this.roomName = room.getRoomName();
        this.userCount = room.getUserCount();
        this.createdAt = room.getCreatedAt();
        this.isInRoom = true;
    }

    // DTO로 변환하는 메서드
    public ChatRoomResponseDto getCurrentRoomAsDto() {
        if (!isInRoom) return null;

        ChatRoomResponseDto dto = new ChatRoomResponseDto();
        dto.setRoomId(roomId);
        dto.setRoomName(roomName);
        dto.setUserCount(userCount);
        dto.setCreatedAt(createdAt);
        return dto;
    }

    public String getCurrentRoomId() {
        return roomId;
    }

    public String getCurrentRoomName() {
        return roomName;
    }

    public boolean isInRoom() {
        return isInRoom && roomId != null;
    }

    public void leaveRoom() {
        this.roomId = null;
        this.roomName = null;
        this.userCount = null;
        this.createdAt = null;
        this.isInRoom = false;
    }

    public void clearSession() {
        leaveRoom();
    }
}
