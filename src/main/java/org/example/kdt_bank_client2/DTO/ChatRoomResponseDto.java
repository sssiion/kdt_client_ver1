package org.example.kdt_bank_client2.DTO;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomResponseDto {
    private String roomId;
    private String roomName;
    private Integer userCount;
    private LocalDateTime createdAt;
}
