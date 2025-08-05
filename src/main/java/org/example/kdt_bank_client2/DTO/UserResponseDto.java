package org.example.kdt_bank_client2.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Data
@NoArgsConstructor
@Setter@Getter
public class UserResponseDto {
    private String userId;
    private String userName;
    private String userPhone;
    private Boolean isOnline;
    private Map<String, String> joinedRooms;
}