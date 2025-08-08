package com.example.KDT_bank_server_project2.manager.DTO;

import com.example.KDT_bank_server_project2.manager.Entity.User;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private String userId;
    private String userName;
    private Boolean isOnline;
    private String userPhone;
    private Map<String, String> joinedRoom;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.isOnline = user.isOnline();
        this.userPhone = user.getUserPhone();
        this.joinedRoom = user.getJoinedRooms();

    }





}
