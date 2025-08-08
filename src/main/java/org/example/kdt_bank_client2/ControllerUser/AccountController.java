package org.example.kdt_bank_client2.ControllerUser;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;

import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.ApiResponse;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.DtoUser.AccountCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.AccountResponseDto;
import org.example.kdt_bank_client2.DtoUser.TransactionRequestDto;
import org.example.kdt_bank_client2.Session.AccountResponseSession;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RequiredArgsConstructor
@RestController
public class AccountController {
    private final ApiClient apiClient;


    // 계좌 생성
    public ApiResponse<AccountResponseDto> creatAccount(AccountCreateRequestDto dto) throws Exception {
        ApiResponse<AccountResponseDto> response = apiClient.post("/api/accounts", dto, new TypeReference<ApiResponse<AccountResponseDto>>() {});
        return response;
    }

    // 모든 계좌 조회
    public ApiResponse<List<AccountResponseDto>> getAllAccounts() throws Exception {
        ApiResponse<List<AccountResponseDto>> response = apiClient.get("/api/accounts", new TypeReference<ApiResponse<List<AccountResponseDto>>>() {});
        return response;
    }
    //계좌번호로 계좌 조회
    public ApiResponse<List<AccountResponseDto>> getAccountByNumber(String accountNumber) throws Exception {
        ApiResponse<List<AccountResponseDto>> response = apiClient.get("/api/accounts/"+accountNumber, new TypeReference<ApiResponse<List<AccountResponseDto>>>() {});
        return response;
    }
    //고객별 계좌 조회
    public ApiResponse<List<AccountResponseDto>> getAccountsByCustomerId(String accountNumber) throws Exception {
        ApiResponse<List<AccountResponseDto>> response = apiClient.get("/api/accounts/"+accountNumber, new TypeReference<ApiResponse<List<AccountResponseDto>>>() {});
        return response;
    }
    //입금
    public ApiResponse<AccountResponseDto> ApiResponseUser(String accountNumber, TransactionRequestDto dto) throws Exception {

        ApiResponse<AccountResponseDto> response = apiClient.post("/api/accounts/"+accountNumber+"/deposit", dto, new TypeReference<ApiResponse<AccountResponseDto>>() {});
        return response;
    }
    //출금
    public ApiResponse<AccountResponseDto> withdraw(String accountNumber, TransactionRequestDto dto) throws Exception {

        ApiResponse<AccountResponseDto> response = apiClient.post("/api/accounts/"+accountNumber+"/withdraw", dto, new TypeReference<ApiResponse<AccountResponseDto>>() {});
        return response;
    }
    //



}
