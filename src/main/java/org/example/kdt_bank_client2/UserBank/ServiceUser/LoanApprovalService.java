package org.example.kdt_bank_client2.UserBank.ServiceUser;


import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ControllerUser.LoanApplicationController;
import org.example.kdt_bank_client2.ControllerUser.LoanAccountController;
import org.example.kdt_bank_client2.DtoUser.LoanApplicationResponseDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanApprovalService {

    private final LoanApplicationController loanApplicationController;
    private final LoanAccountController loanAccountController;

    // 대출 승인 처리
    public void approveApplication(Long applicationId) throws Exception {
        // 승인 API가 별도 있다면 사용, 없으면 직접 데이터변경 및 계좌 입금 처리
        LoanApplicationResponseDto app = loanApplicationController.getById(applicationId);

        // 예시: 계좌 입금 처리(LoanAccountController 등 사용)
        String targetAccountNumber = app.getTargetAccountNumber();
        if (targetAccountNumber != null && app.getRequestedAmount() != null) {
            // 계좌 금액 업데이트 logic 필요 - 실제 로직은 서버 API 구조에 맞게 구현
            // loanAccountController.depositToAccount(targetAccountNumber, app.getRequestedAmount());
        }

        // 상태를 'APPROVED'로 변경하는 API가 있으면 별도로 호출해야 함
        // loanApplicationController.patchStatus(applicationId, "APPROVED");
    }

    // 대출 거절 처리
    public void rejectApplication(Long applicationId) throws Exception {
        // 상태를 'REJECTED'로 변경하는 API 호출 또는 취소
        loanApplicationController.cancel(applicationId);
    }
}
