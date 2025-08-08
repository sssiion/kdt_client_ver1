package com.example.KDT_bank_server_project2.manager.EntityUser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "cash_transaction")
@NoArgsConstructor
@AllArgsConstructor
public class CashTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after_transaction", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfterTransaction;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    public enum TransactionType {
        입금, 출금, 이체;

        public static TransactionType fromString(String type) {
            if (type == null) return 입금;
            try {
                return TransactionType.valueOf(type);
            } catch (IllegalArgumentException e) {
                return 입금;
            }
        }
    }

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }


}