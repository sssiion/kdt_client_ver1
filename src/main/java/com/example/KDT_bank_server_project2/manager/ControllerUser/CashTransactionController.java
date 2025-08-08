package com.example.KDT_bank_server_project2.manager.ControllerUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.CashTransaction;
import com.example.KDT_bank_server_project2.manager.ServiceUser.CashTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class CashTransactionController {

    @Autowired
    private CashTransactionService cashTransactionService;

    // 거래 기록 생성 (입금/출금/이체)
    @PostMapping
    public ResponseEntity<CashTransaction> createTransaction(
            @RequestParam Long accountNumber,
            @RequestParam CashTransaction.TransactionType transactionType,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note) {
        try {
            CashTransaction transaction = cashTransactionService.createTransaction(
                    accountNumber, transactionType, amount, note);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 계좌별 거래 내역 조회
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<CashTransaction>> getTransactionsByAccountNumber(@PathVariable Long accountNumber) {
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    // 계좌별 거래 내역 조회 (페이지네이션)
    @GetMapping("/account/{accountNumber}/paged")
    public ResponseEntity<List<CashTransaction>> getTransactionsByAccountNumberPaged(
            @PathVariable Long accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByAccountNumber(accountNumber, pageable);
        return ResponseEntity.ok(transactions);
    }

    // 거래 유형별 거래 내역 조회
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<List<CashTransaction>> getTransactionsByType(@PathVariable CashTransaction.TransactionType transactionType) {
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByType(transactionType);
        return ResponseEntity.ok(transactions);
    }

    // 특정 계좌의 거래 유형별 내역 조회
    @GetMapping("/account/{accountNumber}/type/{transactionType}")
    public ResponseEntity<List<CashTransaction>> getTransactionsByAccountAndType(
            @PathVariable Long accountNumber,
            @PathVariable CashTransaction.TransactionType transactionType) {
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByAccountAndType(accountNumber, transactionType);
        return ResponseEntity.ok(transactions);
    }

    // 기간별 거래 내역 조회
    @GetMapping("/date-range")
    public ResponseEntity<List<CashTransaction>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    // 특정 계좌의 기간별 거래 내역 조회
    @GetMapping("/account/{accountNumber}/date-range")
    public ResponseEntity<List<CashTransaction>> getTransactionsByAccountAndDateRange(
            @PathVariable Long accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByAccountAndDateRange(
                accountNumber, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    // 특정 계좌의 거래 건수 조회
    @GetMapping("/account/{accountNumber}/count")
    public ResponseEntity<Long> getTransactionCountByAccountNumber(@PathVariable Long accountNumber) {
        long count = cashTransactionService.getTransactionCountByAccountNumber(accountNumber);
        return ResponseEntity.ok(count);
    }

    // 특정 기간 동안 거래 유형별 총액 조회
    @GetMapping("/account/{accountNumber}/total-amount")
    public ResponseEntity<BigDecimal> getTotalAmountByTypeAndPeriod(
            @PathVariable Long accountNumber,
            @RequestParam CashTransaction.TransactionType transactionType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal totalAmount = cashTransactionService.getTotalAmountByTypeAndPeriod(
                accountNumber, transactionType, startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    // 거래 메모로 검색
    @GetMapping("/search")
    public ResponseEntity<List<CashTransaction>> searchTransactionsByNote(@RequestParam String keyword) {
        List<CashTransaction> transactions = cashTransactionService.searchTransactionsByNote(keyword);
        return ResponseEntity.ok(transactions);
    }

    // 특정 계좌의 최근 거래 조회
    @GetMapping("/account/{accountNumber}/latest")
    public ResponseEntity<CashTransaction> getLatestTransactionByAccountNumber(@PathVariable Long accountNumber) {
        CashTransaction transaction = cashTransactionService.getLatestTransactionByAccountNumber(accountNumber);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 이체 처리
    @PostMapping("/transfer")
    public ResponseEntity<String> processTransfer(
            @RequestParam Long fromAccountNumber,
            @RequestParam Long toAccountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note) {
        try {
            cashTransactionService.processTransfer(fromAccountNumber, toAccountNumber, amount, note);
            return ResponseEntity.ok("이체가 성공적으로 처리되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("이체 처리 실패: " + e.getMessage());
        }
    }

    // 최소 금액 이상 거래 조회
    @GetMapping("/account/{accountNumber}/min-amount/{minAmount}")
    public ResponseEntity<List<CashTransaction>> getTransactionsByMinAmount(
            @PathVariable Long accountNumber,
            @PathVariable BigDecimal minAmount) {
        List<CashTransaction> transactions = cashTransactionService.getTransactionsByMinAmount(accountNumber, minAmount);
        return ResponseEntity.ok(transactions);
    }
}
