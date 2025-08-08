package com.example.KDT_bank_server_project2.manager.EntityUser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@Table(name = "loan_application")
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "target_account_number")
    private Long targetAccountNumber;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED, CANCELLED;

        public static ApplicationStatus fromString(String status) {
            if (status == null) return PENDING;
            try {
                return ApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return PENDING;
            }
        }
    }

    @PrePersist
    protected void onCreate() {
        applicationDate = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }

}
