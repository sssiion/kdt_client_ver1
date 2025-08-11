package org.example.kdt_bank_client2.ControllerUser;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;

import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.ApiResponse;
import org.example.kdt_bank_client2.DTO.UserResponseDto;
import org.example.kdt_bank_client2.DtoUser.*;
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
    public ApiResponseUser<AccountResponseDto> creatAccount(AccountCreateRequestDto dto) throws Exception {
        ApiResponseUser<AccountResponseDto> response = apiClient.post("/api/accounts", dto, new TypeReference<ApiResponseUser<AccountResponseDto>>() {});
        return response;
    }

    // 모든 계좌 조회
    public ApiResponseUser<List<AccountResponseDto>> getAllAccounts() throws Exception {
        ApiResponseUser<List<AccountResponseDto>> response = apiClient.get("/api/accounts", new TypeReference<ApiResponseUser<List<AccountResponseDto>>>() {});
        return response;
    }
    //계좌번호로 계좌 조회
    public AccountResponseDto getAccountByNumber(String accountNumber) throws Exception {
        ApiResponseUser<AccountResponseDto> response = apiClient.get("/api/accounts/"+accountNumber, new TypeReference<ApiResponseUser<AccountResponseDto>>() {});
        return response.getData();
    }
    //고객별 계좌 조회
    public ApiResponseUser<List<AccountResponseDto>> getAccountsByCustomerId(String accountNumber) throws Exception {
        ApiResponseUser<List<AccountResponseDto>> response = apiClient.get("/api/accounts/"+accountNumber, new TypeReference<ApiResponseUser<List<AccountResponseDto>>>() {});
        return response;
    }
    //입금
    public CashTransactionResponseDto deposit(TransferRequestDto dto,String userId) throws Exception {

        ApiResponseUser<CashTransactionResponseDto> response = apiClient.post("/api/accounts/deposit/"+userId, dto, new TypeReference<ApiResponseUser<CashTransactionResponseDto>>() {});
        return response.getData();
    }
    //출금
    public CashTransactionResponseDto withdraw(TransferRequestDto dto,String userId) throws Exception {

        ApiResponseUser<CashTransactionResponseDto> response = apiClient.post("/api/accounts/withdraw"+userId, dto, new TypeReference<ApiResponseUser<CashTransactionResponseDto>>() {});
        return response.getData();
    }
    //송금
    public CashTransactionResponseDto remittance(TransferRequestDto dto,String userId) throws Exception {
        ApiResponseUser<CashTransactionResponseDto> response = apiClient.post("/api/accounts/remittance"+userId, dto,new TypeReference<ApiResponseUser<CashTransactionResponseDto>>() {});
        return response.getData();
    }
    //고객 아이디로 고객 모든 계좌 조회
    public List<AccountResponseDto>  getAccountById(String customerId) throws Exception {
        ApiResponseUser<List<AccountResponseDto>> response = apiClient.get("/api/accounts/id/"+customerId, new TypeReference<ApiResponseUser<List<AccountResponseDto>>>() {});
        return response.getData();
    }

    //계좌 삭제
    public void deleteAccount(String accountNumber) throws Exception {
        apiClient.get("/api/accounts/delete/"+accountNumber, new TypeReference<ApiResponseUser>() {});
    }

}
