package org.example.kdt_bank_client2.UserBank.ServiceUser;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ControllerUser.LoanApplicationController;
import org.example.kdt_bank_client2.DtoUser.LoanApplicationCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.LoanApplicationResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationController loanApplicationController;

    // 대출 신청 생성
    public LoanApplicationResponseDto createLoanApplication(LoanApplicationCreateRequestDto dto) throws Exception {
        return loanApplicationController.create(dto);
    }

    // 대출 신청 목록 (전체)
    public List<LoanApplicationResponseDto> getApplicationsByCustomerId(Long customerId) throws Exception {
        return loanApplicationController.listByCustomerId(customerId);
    }

    // 특정 신청 조회
    public LoanApplicationResponseDto getApplicationById(Long id) throws Exception {
        return loanApplicationController.getById(id);
    }

    // 신청 취소
    public LoanApplicationResponseDto cancelApplication(Long id) throws Exception {
        return loanApplicationController.cancel(id);
    }

    // 현재 Pending 상태 신청 목록 조회
    public List<LoanApplicationResponseDto> getPendingApplications() throws Exception {
        // status = 'PENDING'인 것만 필터
        List<LoanApplicationResponseDto> all = loanApplicationController.listByCustomerId(null);
        return all.stream()
                .filter(app -> "PENDING".equalsIgnoreCase(app.getStatus()))
                .collect(Collectors.toList());
    }

    // 상태 변경 (승인/거절)
    public void updateStatus(String applicationId, String status) throws Exception {
        // API에 따라 다르게 처리 (예: approve/reject 엔드포인트 필요)
        // 예시 (직접적으로 엔드포인트가 있으면 call, 없으면 patch 등)
        // loanApplicationController.updateStatus(applicationId, status);

        // 실제 패치 엔드포인트가 있으면 아래처럼:
        // loanApplicationController.patchStatus(applicationId, status);

        // 여기서는 임시: 취소 함수만 있는 경우 reject는 cancel로 대체 가능
        if ("REJECTED".equalsIgnoreCase(status)) {
            loanApplicationController.cancel(Long.valueOf(applicationId));
        }
        // 승인 처리(approve)는 별도 구현 필요
    }
}
