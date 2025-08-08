package org.example.kdt_bank_client2.DtoUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 계좌 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class AccountResponseDto {

    private Long accountNumber;
    private Long customerId;
    private String customerName;
    private String productName;
    private BigDecimal amount;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private Account.AccountStatus status;
    private String productType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountResponseDto(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.customerId = account.getCustomerId();
        this.customerName = null; // 별도로 설정 필요
        this.productName = account.getProductName();
        this.amount = account.getAmount();
        this.openingDate = account.getOpeningDate();
        this.closingDate = account.getClosingDate();
        this.status = account.getStatus();
        this.productType = account.getProductType();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
        System.out.println("AccountResponseDto 생성: 계좌번호 " + accountNumber +
                ", 고객ID: " + customerId +
                ", 상품: " + productName +
                ", 잔액: " + amount +
                ", 상태: " + status);
    }
}
