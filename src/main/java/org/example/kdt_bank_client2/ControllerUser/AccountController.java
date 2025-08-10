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
    public AccountResponseDto getAccountByNumber(String accountNumber) throws Exception {
        ApiResponse<AccountResponseDto> response = apiClient.get("/api/accounts/"+accountNumber, new TypeReference<ApiResponse<AccountResponseDto>>() {});
        return response.getData();
    }
    //고객별 계좌 조회
    public ApiResponse<List<AccountResponseDto>> getAccountsByCustomerId(String accountNumber) throws Exception {
        ApiResponse<List<AccountResponseDto>> response = apiClient.get("/api/accounts/"+accountNumber, new TypeReference<ApiResponse<List<AccountResponseDto>>>() {});
        return response;
    }
    //입금
    public CashTransactionResponseDto deposit(TransferRequestDto dto) throws Exception {

        ApiResponse<CashTransactionResponseDto> response = apiClient.post("/api/accounts/"+dto.getToAccountNumber()+"/deposit", dto, new TypeReference<ApiResponse<CashTransactionResponseDto>>() {});
        return response.getData();
    }
    //출금
    public CashTransactionResponseDto withdraw(TransferRequestDto dto) throws Exception {

        ApiResponse<CashTransactionResponseDto> response = apiClient.post("/api/accounts/"+dto.getFromAccountNumber()+"/withdraw", dto, new TypeReference<ApiResponse<CashTransactionResponseDto>>() {});
        return response.getData();
    }
    //송금
    public CashTransactionResponseDto remittance(TransferRequestDto dto) throws Exception {
        ApiResponse<CashTransactionResponseDto> reponse = apiClient.post("api/accounts/remittance", dto,new TypeReference<ApiResponse<CashTransactionResponseDto>>() {});
        return reponse.getData();
    }
    //고객 아이디로 고객 모든 계좌 조회
    public List<AccountResponseDto>  getAccountById(String customerId) throws Exception {
        ApiResponse<List<AccountResponseDto>> response = apiClient.get("/api/account/"+customerId, new TypeReference<ApiResponse<List<AccountResponseDto>>>() {});
        return response.getData();
    }



}
