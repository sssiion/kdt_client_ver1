package org.example.kdt_bank_client2.DtoUser;

import com.example.KDT_bank_server_project2.manager.EntityUser.BankEmployee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


// 직원 생성 요청 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class EmployeeCreateRequestDto {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @NotBlank(message = "부서는 필수입니다")
    private String department;

    private BankEmployee.EmployeeRole role;
    private String phone;
}
