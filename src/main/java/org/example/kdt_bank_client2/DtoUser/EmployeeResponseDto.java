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
    private BankEmployee.EmployeeRole role;
    private String phone;
    private BankEmployee.EmployeeStatus status;
    private LocalDateTime createdAt;

    public EmployeeResponseDto(BankEmployee employee) {
        this.employeeId = employee.getEmployeeId();
        this.name = employee.getName();
        this.email = employee.getEmail();
        this.department = employee.getDepartment();
        this.role = employee.getRole();
        this.phone = employee.getPhone();
        this.status = employee.getStatus();
        this.createdAt = employee.getCreatedAt();
        System.out.println("EmployeeResponseDto 생성: " + name +
                ", department: " + department +
                ", role: " + role +
                ", status: " + status);
    }
    public static EmployeeResponseDto from(BankEmployee customer) {
        return new EmployeeResponseDto(customer);
    }
}
