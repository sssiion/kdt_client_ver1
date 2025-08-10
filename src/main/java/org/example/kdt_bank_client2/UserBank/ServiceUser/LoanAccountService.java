package org.example.kdt_bank_client2.UserBank.ServiceUser;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ControllerUser.LoanAccountController;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
}
