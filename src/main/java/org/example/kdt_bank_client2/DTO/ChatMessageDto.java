package org.example.kdt_bank_client2.DTO;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageDto {
    private String roomId;
    private String senderId;
    private String senderName;
    private String message;
    private String messageType;
    private LocalDateTime sentAt;
    private boolean isMyMessage;
}
