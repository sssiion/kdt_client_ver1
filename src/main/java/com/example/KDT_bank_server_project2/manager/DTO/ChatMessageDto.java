package com.example.KDT_bank_server_project2.manager.DTO;

import com.example.KDT_bank_server_project2.manager.Entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String roomId;
    private String userId;
    private String content;
    private String type;

    private String sentAt;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.roomId = chatMessage.getRoomUuid();
        this.userId = chatMessage.getSenderId();
        this.content = chatMessage.getContent();
        this.type = chatMessage.getType().toString();
        this.sentAt = chatMessage.getSentAt().toString();


    }



}

