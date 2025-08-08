package org.example.kdt_bank_client2.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.kdt_bank_client2.UI.FriendListController;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserDataDto {
    private String userId;

    private String userName;
    private Boolean isOnline;
    private String userPhone;
    private FriendListController.UserType userType;
    private FriendListController.UserStatus status;
    public UserDataDto() {}


}
