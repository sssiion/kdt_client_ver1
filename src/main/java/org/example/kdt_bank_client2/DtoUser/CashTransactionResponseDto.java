package org.example.kdt_bank_client2.DtoUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.CashTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 현금 거래 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class CashTransactionResponseDto {

    private Long transactionId;
    private Long accountNumber;
    private CashTransaction.TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;
    private String note;
    private LocalDateTime transactionDate;

    public CashTransactionResponseDto(CashTransaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.accountNumber = transaction.getAccountNumber();
        this.transactionType = transaction.getTransactionType();
        this.amount = transaction.getAmount();
        this.balanceAfterTransaction = transaction.getBalanceAfterTransaction();
        this.note = transaction.getNote();
        this.transactionDate = transaction.getTransactionDate();
        System.out.println("CashTransactionResponseDto 생성: 거래ID " + transactionId +
                ", 계좌번호: " + accountNumber +
                ", 거래유형: " + transactionType +
                ", 금액: " + amount +
                ", 거래후잔액: " + balanceAfterTransaction);
    }
}
