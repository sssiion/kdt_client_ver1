package org.example.kdt_bank_client2.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private String roomId;
    private String roomName;
    private Integer userCount;
    private String createdAt;
}
