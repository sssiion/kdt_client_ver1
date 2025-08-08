package com.example.KDT_bank_server_project2.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserLogoutDto {
    private String userId;
}
