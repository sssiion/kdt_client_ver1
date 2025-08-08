package org.example.kdt_bank_client2.DtoUser;

import com.example.KDT_bank_server_project2.manager.EntityUser.BankEmployee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 직원 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class EmployeeResponseDto {

    private Long employeeId;
    private String name;
    private String email;
    private String department;
    private String role;
    private String phone;
    private String status;
    private LocalDateTime createdAt;

}
