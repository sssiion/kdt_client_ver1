package com.example.KDT_bank_server_project2.manager.EntityUser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "loan_account")
@NoArgsConstructor
@AllArgsConstructor
public class LoanAccount {
    @Id
    @Column(name = "loan_id", length = 20)
    private String loanId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "repayment_amount", precision = 15, scale = 2)
    private BigDecimal repaymentAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum LoanStatus {
        ACTIVE, PAID_OFF, DEFAULT;

        public static LoanStatus fromString(String status) {
            if (status == null) return ACTIVE;
            try {
                return LoanStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ACTIVE;
            }
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LoanStatus.ACTIVE;
        }
        if (repaymentAmount == null) {
            repaymentAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}