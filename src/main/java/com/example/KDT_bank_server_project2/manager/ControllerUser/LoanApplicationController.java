package com.example.KDT_bank_server_project2.manager.ControllerUser;

import com.example.KDT_bank_server_project2.manager.EntityUser.LoanApplication;
import com.example.KDT_bank_server_project2.manager.ServiceUser.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loan-applications")
@CrossOrigin(origins = "*")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    // 대출 신청 생성
    @PostMapping
    public ResponseEntity<LoanApplication> createLoanApplication(@RequestBody LoanApplication loanApplication) {
        try {
            LoanApplication createdApplication = loanApplicationService.createLoanApplication(loanApplication);
            return ResponseEntity.ok(createdApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 모든 대출 신청 조회
    @GetMapping
    public ResponseEntity<List<LoanApplication>> getAllLoanApplications() {
        List<LoanApplication> applications = loanApplicationService.getAllLoanApplications();
        return ResponseEntity.ok(applications);
    }

    // ID로 대출 신청 조회
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> getLoanApplicationById(@PathVariable Long id) {
        Optional<LoanApplication> application = loanApplicationService.getLoanApplicationById(id);
        return application.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 고객별 대출 신청 조회
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanApplication>> getLoanApplicationsByCustomerId(@PathVariable Long customerId) {
        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByCustomerId(customerId);
        return ResponseEntity.ok(applications);
    }

    // 상태별 대출 신청 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplication>> getLoanApplicationsByStatus(@PathVariable LoanApplication.ApplicationStatus status) {
        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    // 대기 중인 대출 신청 조회
    @GetMapping("/pending")
    public ResponseEntity<List<LoanApplication>> getPendingApplications() {
        List<LoanApplication> applications = loanApplicationService.getPendingApplications();
        return ResponseEntity.ok(applications);
    }

    // 상품별 대출 신청 조회
    @GetMapping("/product/{productName}")
    public ResponseEntity<List<LoanApplication>> getLoanApplicationsByProductName(@PathVariable String productName) {
        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByProductName(productName);
        return ResponseEntity.ok(applications);
    }

    // 대출 신청 승인
    @PatchMapping("/{applicationId}/approve")
    public ResponseEntity<LoanApplication> approveLoanApplication(@PathVariable Long applicationId,
                                                                  @RequestParam Long approverId) {
        try {
            LoanApplication approvedApplication = loanApplicationService.approveLoanApplication(applicationId, approverId);
            return ResponseEntity.ok(approvedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 대출 신청 거절
    @PatchMapping("/{applicationId}/reject")
    public ResponseEntity<LoanApplication> rejectLoanApplication(@PathVariable Long applicationId,
                                                                 @RequestParam String rejectionReason) {
        try {
            LoanApplication rejectedApplication = loanApplicationService.rejectLoanApplication(applicationId, rejectionReason);
            return ResponseEntity.ok(rejectedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 대출 신청 취소
    @PatchMapping("/{applicationId}/cancel")
    public ResponseEntity<LoanApplication> cancelLoanApplication(@PathVariable Long applicationId) {
        try {
            LoanApplication cancelledApplication = loanApplicationService.cancelLoanApplication(applicationId);
            return ResponseEntity.ok(cancelledApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 고객의 대기 중인 대출 신청 개수 조회
    @GetMapping("/customer/{customerId}/pending-count")
    public ResponseEntity<Long> getPendingApplicationCountByCustomerId(@PathVariable Long customerId) {
        long count = loanApplicationService.getPendingApplicationCountByCustomerId(customerId);
        return ResponseEntity.ok(count);
    }

    // 기간별 신청 조회
    @GetMapping("/date-range")
    public ResponseEntity<List<LoanApplication>> getApplicationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<LoanApplication> applications = loanApplicationService.getApplicationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(applications);
    }

    // 기간별 승인 조회
    @GetMapping("/approvals/date-range")
    public ResponseEntity<List<LoanApplication>> getApprovalsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<LoanApplication> applications = loanApplicationService.getApprovalsByDateRange(startDate, endDate);
        return ResponseEntity.ok(applications);
    }

    // 특정 기간 동안 승인된 총 대출 금액
    @GetMapping("/approvals/total-amount")
    public ResponseEntity<BigDecimal> getTotalApprovedAmountByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal totalAmount = loanApplicationService.getTotalApprovedAmountByPeriod(startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    // 최소 신청 금액 이상인 대출 신청 조회
    @GetMapping("/min-amount/{minAmount}")
    public ResponseEntity<List<LoanApplication>> getApplicationsByMinAmount(@PathVariable BigDecimal minAmount) {
        List<LoanApplication> applications = loanApplicationService.getApplicationsByMinAmount(minAmount);
        return ResponseEntity.ok(applications);
    }

    // 상품별 및 상태별 대출 신청 조회 (페이지네이션)
    @GetMapping("/product/{productName}/status/{status}")
    public ResponseEntity<List<LoanApplication>> getApplicationsByProductAndStatus(
            @PathVariable String productName,
            @PathVariable LoanApplication.ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<LoanApplication> applications = loanApplicationService.getApplicationsByProductAndStatus(
                productName, status, pageable);
        return ResponseEntity.ok(applications);
    }
}
