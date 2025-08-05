package org.example.kdt_bank_client2.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class createRoomDto {
    private String roomName;

    public createRoomDto(String roomName) {
        this.roomName = roomName;
    }
}