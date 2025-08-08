package org.example.kdt_bank_client2.DTO;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String userId;
    private String userName;
    private Boolean isOnline;
    private String userPhone;
    private Map<String, String> joinedRoom;


}