package com.example.KDT_bank_server_project2.manager.EntityUser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@Table(name = "bank_employee")
@NoArgsConstructor
@AllArgsConstructor
public class BankEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String department;

    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EmployeeRole {
        MANAGER, STAFF, ADMIN;

        public static EmployeeRole fromString(String role) {
            if (role == null) return STAFF;
            try {
                return EmployeeRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                return STAFF;
            }
        }
    }

    public enum EmployeeStatus {
        ACTIVE, INACTIVE;

        public static EmployeeStatus fromString(String status) {
            if (status == null) return ACTIVE;
            try {
                return EmployeeStatus.valueOf(status.toUpperCase());
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
            status = EmployeeStatus.ACTIVE;
        }
        if (role == null) {
            role = EmployeeRole.STAFF;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}