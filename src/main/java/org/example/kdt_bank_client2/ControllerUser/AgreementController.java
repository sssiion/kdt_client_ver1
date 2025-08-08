package org.example.kdt_bank_client2.ControllerUser;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.ApiResponse;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.AgreementResponseDto;
import org.example.kdt_bank_client2.DtoUser.TransactionRequestDto;
import org.example.kdt_bank_client2.UserBank.Agreement;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RequiredArgsConstructor
@RestController
public class AgreementController {
    private final ApiClient apiClient;

    //약정생성
    public Agreement createAgreement(Agreement agreement) throws Exception {

        Agreement response = apiClient.post("/api/agreements/", agreement, new TypeReference<Agreement>() {});
        return response;
    }
    //모든 약정 조회
    public List<Agreement> getAllAgreements() throws Exception {

        List<Agreement> response = apiClient.get("/api/agreements/", new TypeReference<List<Agreement>>() {});
        return response;
    }
    // ID로 약정 조회
    public Agreement getAgreementById(String id) throws Exception {

        Agreement response = apiClient.get("/api/agreements/"+id, new TypeReference<Agreement>() {});
        return response;
    }
    // 고객별 약정 조회
    public List<Agreement> getAgreementsByCustomerId(String customerId) throws Exception {

        List<Agreement> response = apiClient.get("/api/agreements/customer/"+customerId, new TypeReference<List<Agreement>>() {});
        return response;
    }
    // 고객의 활성 약정 조회
    public List<Agreement> getActiveAgreementsByCustomerId(String customerId) throws Exception {

        List<Agreement> response = apiClient.get("/api/agreements/"+customerId+"/active", new TypeReference<List<Agreement>>() {});
        return response;
    }
    // 상품별 약정 조회
    public List<Agreement> getAgreementsByProductName(String productName) throws Exception {

        List<Agreement> response = apiClient.get("/api/agreements/product/"+productName, new TypeReference<List<Agreement>>() {});
        return response;
    }
    // 상태별 약정 조회
    public List<Agreement> getAgreementsByStatus(String status) throws Exception {

        List<Agreement> response = apiClient.get("/api/agreements/status/"+status, new TypeReference<List<Agreement>>() {});
        return response;
    }

    // 약정 정보 수정
    public Agreement updateAgreement(String id, AgreementResponseDto dto) throws Exception {

        Agreement response = apiClient.post("/api/agreements/"+id, dto, new TypeReference<Agreement>() {});
        return response;
    }

    // 약정 상태 변경
    public Agreement updateAgreementStatus(String id, String status) throws Exception {

        Agreement response = apiClient.post("/api/agreements/"+id+"/status", status, new TypeReference<Agreement>() {});
        return response;
    }
    // 약정 연장
    public Agreement extendAgreement(String id, String newExpirationDate) throws Exception {

        Agreement response = apiClient.post("/api/agreements/"+id+"/extend", newExpirationDate, new TypeReference<Agreement>() {});
        return response;
    }

}
