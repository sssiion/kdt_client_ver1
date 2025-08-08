package org.example.kdt_bank_client2.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class ChatMessageDto {
    private String roomId;
    // 🔥 확인 필요: 서버에서는 "userId"를 사용하지만 클라이언트에서는 "senderId" 사용
    // 서버 API와 맞추려면 다음 중 하나를 선택:
    // 1) senderId → userId로 변경, senderName → 제거 또는 추가 필드로 관리
    // 2) 서버 DTO에 senderId, senderName 필드 추가

    private String userId;      // 서버 API에 맞춤
    private String content;     // 서버에서는 "content", 클라이언트에서는 "message"
    private String type;        // 서버에서는 "type", 클라이언트에서는 "messageType"

    private String sentAt;

    //public LocalDateTime getSentAt() {
        //return LocalDateTime.parse(sentAt);
    //}

}
