package com.example.KDT_bank_server_project2.manager.DTO;

import com.example.KDT_bank_server_project2.manager.Entity.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponseDto {
    private String roomId;
    private String roomName;
    private Integer userCount;
    private String createdAt;

    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.roomName = chatRoom.getRoomName();
        this.userCount = chatRoom.getUserCount();
        this.createdAt = chatRoom.getCreatedAt().toString();
    }
}
