package org.example.kdt_bank_client2.DtoUser;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 고객 요약 DTO (목록용)
@Data
@Getter
@Setter
@AllArgsConstructor
public class CustomerSummaryDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String status;


}
