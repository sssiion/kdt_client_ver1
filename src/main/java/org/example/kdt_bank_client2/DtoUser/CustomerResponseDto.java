package org.example.kdt_bank_client2.DtoUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 고객 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class CustomerResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String residentNumber;
    private String address;
    private Customer.CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerResponseDto(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
        this.residentNumber = customer.getResidentNumber();
        this.address = customer.getAddress();
        this.status = customer.getStatus();
        this.createdAt = customer.getCreatedAt();
        this.updatedAt = customer.getUpdatedAt();
        System.out.println("CustomerResponseDto 생성: " + name +
                ", email: " + email +
                ", status: " + status);
    }
    // 기존 컨트롤러 호환을 위한 from() 메서드 추가
    public static CustomerResponseDto from(Customer customer) {
        return new CustomerResponseDto(customer);
    }
}
