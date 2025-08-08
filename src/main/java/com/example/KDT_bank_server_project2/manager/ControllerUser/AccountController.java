package com.example.KDT_bank_server_project2.manager.ControllerUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.Account;
import com.example.KDT_bank_server_project2.manager.ServiceUser.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 계좌 생성
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);
            return ResponseEntity.ok(createdAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 모든 계좌 조회
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // 계좌번호로 계좌 조회
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable Long accountNumber) {
        Optional<Account> account = accountService.getAccountByNumber(accountNumber);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 고객별 계좌 조회
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    // 고객의 활성 계좌만 조회
    @GetMapping("/customer/{customerId}/active")
    public ResponseEntity<List<Account>> getActiveAccountsByCustomerId(@PathVariable Long customerId) {
        List<Account> accounts = accountService.getActiveAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    // 계좌 잔액 조회
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long accountNumber) {
        try {
            BigDecimal balance = accountService.getAccountBalance(accountNumber);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 입금
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long accountNumber,
                                           @RequestParam BigDecimal amount) {
        try {
            Account updatedAccount = accountService.deposit(accountNumber, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 출금
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long accountNumber,
                                            @RequestParam BigDecimal amount) {
        try {
            Account updatedAccount = accountService.withdraw(accountNumber, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 계좌 상태 변경
    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<Account> updateAccountStatus(@PathVariable Long accountNumber,
                                                       @RequestParam Account.AccountStatus status) {
        try {
            Account updatedAccount = accountService.updateAccountStatus(accountNumber, status);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 상품별 계좌 조회
    @GetMapping("/product/{productName}")
    public ResponseEntity<List<Account>> getAccountsByProductName(@PathVariable String productName) {
        List<Account> accounts = accountService.getAccountsByProductName(productName);
        return ResponseEntity.ok(accounts);
    }

    // 고객의 활성 계좌 개수 조회
    @GetMapping("/customer/{customerId}/count")
    public ResponseEntity<Long> getActiveAccountCountByCustomerId(@PathVariable Long customerId) {
        long count = accountService.getActiveAccountCountByCustomerId(customerId);
        return ResponseEntity.ok(count);
    }

    // 계좌 해지
    @PatchMapping("/{accountNumber}/close")
    public ResponseEntity<Account> closeAccount(@PathVariable Long accountNumber) {
        try {
            Account closedAccount = accountService.closeAccount(accountNumber);
            return ResponseEntity.ok(closedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

