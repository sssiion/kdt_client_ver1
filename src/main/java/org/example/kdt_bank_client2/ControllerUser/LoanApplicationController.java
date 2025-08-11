package org.example.kdt_bank_client2.ControllerUser;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.ApiResponse;
import org.example.kdt_bank_client2.DtoUser.LoanApplicationCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.LoanApplicationResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanApplicationController {

    private final ApiClient api;

    // POST /api/loan-applications
    public LoanApplicationResponseDto create(LoanApplicationCreateRequestDto dto) throws Exception {
        return api.post("/api/loan-applications",
                dto,
                new TypeReference<LoanApplicationResponseDto>() {});
    }

    // GET /api/loan-applications?customerId=123
    public List<LoanApplicationResponseDto> listByCustomerId(Long customerId) throws Exception {
        return api.get("/api/loan-applications?customerId=" + customerId,
                new TypeReference<List<LoanApplicationResponseDto>>() {});
    }

    // GET /api/loan-applications/{id}
    public LoanApplicationResponseDto getById(Long id) throws Exception {
        return api.get("/api/loan-applications/" + id,
                new TypeReference<LoanApplicationResponseDto>() {});
    }

    // PATCH /api/loan-applications/{id}/cancel
    public LoanApplicationResponseDto cancel(Long id) throws Exception {
        // 바디 필요 없으면 null
        return api.patch("/api/loan-applications/" + id + "/cancel",
                null,
                new TypeReference<LoanApplicationResponseDto>() {});
    }
}
