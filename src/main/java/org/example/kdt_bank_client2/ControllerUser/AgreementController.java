package org.example.kdt_bank_client2.ControllerUser;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.DtoUser.AgreementResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RequiredArgsConstructor
@RestController
public class AgreementController {
    private final ApiClient apiClient;

    //약정생성
    public AgreementResponseDto createAgreement(AgreementResponseDto agreement) throws Exception {

        AgreementResponseDto response = apiClient.post("/api/agreements/", agreement, new TypeReference<AgreementResponseDto>() {});
        return response;
    }
    //모든 약정 조회
    public List<AgreementResponseDto> getAllAgreements() throws Exception {

        List<AgreementResponseDto> response = apiClient.get("/api/agreements/", new TypeReference<List<AgreementResponseDto>>() {});
        return response;
    }
    // ID로 약정 조회
    public AgreementResponseDto getAgreementById(String id) throws Exception {

        AgreementResponseDto response = apiClient.get("/api/agreements/"+id, new TypeReference<AgreementResponseDto>() {});
        return response;
    }
    // 고객별 약정 조회
    public List<AgreementResponseDto> getAgreementsByCustomerId(String customerId) throws Exception {

        List<AgreementResponseDto> response = apiClient.get("/api/agreements/customer/"+customerId, new TypeReference<List<AgreementResponseDto>>() {});
        return response;
    }

    // 상품별 약정 조회
    public List<AgreementResponseDto> getAgreementsByProductName(String productName) throws Exception {

        List<AgreementResponseDto> response = apiClient.get("/api/agreements/product/"+productName, new TypeReference<List<AgreementResponseDto>>() {});
        return response;
    }


    // 약정 정보 수정
    public AgreementResponseDto updateAgreement(String id, AgreementResponseDto dto) throws Exception {

        AgreementResponseDto response = apiClient.post("/api/agreements/"+id, dto, new TypeReference<AgreementResponseDto>() {});
        return response;
    }

    // 약정 연장
    public AgreementResponseDto extendAgreement(String id, String newExpirationDate) throws Exception {

        AgreementResponseDto response = apiClient.post("/api/agreements/"+id+"/extend", newExpirationDate, new TypeReference<AgreementResponseDto>() {});
        return response;
    }

}
