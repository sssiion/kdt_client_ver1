package org.example.kdt_bank_client2.UserBank.ServiceUser;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ControllerUser.LoanAccountController;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanAccountService {
    private final LoanAccountController loanAccountController;

    // 고객의 대출 계좌 전체 조회
    public List<LoanAccountResponseDto> getLoanAccountsByCustomerId(String customerId) {
        try {
            return loanAccountController.getLoanAccountsByCustomerId(customerId);
        } catch (Exception e) {
            throw new RuntimeException("대출 계좌 조회 실패: " + e.getMessage(), e);
        }
    }

    // 대출 상환 처리
    public LoanAccountResponseDto makeRepayment(String loanId, BigDecimal repaymentAmount) {
        try {
            return loanAccountController.makeRepayment(loanId, repaymentAmount);
        } catch (Exception e) {
            throw new RuntimeException("상환 처리 실패: " + e.getMessage(), e);
        }
    }
    // 🔹 [추가] 만기 임박 대출 계좌 조회
    public List<LoanAccountResponseDto> getLoansNearingMaturity(LocalDate date) {
        try {
            return loanAccountController.getLoansNearingMaturity(date);
        } catch (Exception e) {
            throw new RuntimeException("만기 임박 대출 조회 실패: " + e.getMessage(), e);
        }
    }

    // 🔹 [추가] 상품명으로 대출 계좌 조회
    public List<LoanAccountResponseDto> getLoanAccountsByProductName(String productName) {
        try {
            return loanAccountController.getLoanAccountsByProductName(productName);
        } catch (Exception e) {
            throw new RuntimeException("상품명으로 대출 계좌 조회 실패: " + e.getMessage(), e);
        }
    }

    // 🔹 [선택] 특정 대출 ID로 상세 대출 조회
    public LoanAccountResponseDto getLoanAccountById(String loanId) {
        try {
            return loanAccountController.getLoanAccountById(loanId);
        } catch (Exception e) {
            throw new RuntimeException("대출 상세 조회 실패: " + e.getMessage(), e);
        }
    }
}

