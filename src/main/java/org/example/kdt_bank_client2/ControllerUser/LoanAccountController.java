package org.example.kdt_bank_client2.ControllerUser;


import com.fasterxml.jackson.core.type.TypeReference;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.DtoUser.ApiResponseUser;
import org.example.kdt_bank_client2.DtoUser.LoanAccountCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.LoanAccountResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 대출 계좌 관련 서버 API 호출을 담당하는 세션 계층입니다.
 * ApiClient를 사용하여 실제 HTTP 요청을 수행하고, 서버 응답을 클라이언트 DTO/엔티티로 변환합니다.
 * 이 계층은 컨트롤러와 ApiClient 사이에서 비즈니스 로직과 통신 로직을 분리하는 역할을 합니다.
 */
@Component
public class LoanAccountController {

    private final ApiClient apiClient;


    public LoanAccountController(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    //   대출 계좌 생성
    public LoanAccountResponseDto createLoanAccount(LoanAccountCreateRequestDto loanAccount) throws Exception {
        // POST 요청을 통해 /api/loan-accounts 엔드포인트로 loanAccount 객체를 전송합니다.
        return apiClient.post("/api/loan-accounts", loanAccount, new TypeReference<LoanAccountResponseDto>() {});
    }
    //대출 만기일 변경
    public LoanAccountResponseDto changeExpireDate(LoanAccountCreateRequestDto loanAccount) throws Exception {
        return apiClient.post("/api/loan-accounts/expiredate", loanAccount, new TypeReference<LoanAccountResponseDto>() {});
    }

    //    모든 대출 계자 조회
    public List<LoanAccountResponseDto> getAllLoanAccounts() throws Exception {
        // GET 요청을 통해 /api/loan-accounts 엔드포인트로 모든 대출 계좌를 조회합니다.
        return apiClient.get("/api/loan-accounts", new TypeReference<List<LoanAccountResponseDto>>() {});
    }

    //    대출 id 사용 하여 특정태출계좌 조회
    public LoanAccountResponseDto getLoanAccountById(String loanId) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/{loanId} 엔드포인트로 대출 계좌를 조회합니다.
        return apiClient.get("/api/loan-accounts/" + loanId, new TypeReference<LoanAccountResponseDto>() {});
    }

    //    해당 고객의 모든 대출 계좌 조회
    public List<LoanAccountResponseDto> getLoanAccountsByCustomerId(String customerId) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/customer/{customerId} 엔드포인트로 고객별 대출 계좌를 조회합니다.
        return apiClient.get("/api/loan-accounts/customer/" + customerId, new TypeReference<List<LoanAccountResponseDto>>() {});
    }


    /**
     * 대출 ID를 사용하여 대출 잔액을 서버로부터 조회합니다.
     * @param loanId 조회할 대출의 고유 ID
     * @return 서버로부터 받은 대출 잔액 (BigDecimal)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public BigDecimal getLoanBalance(String loanId) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/{loanId}/balance 엔드포인트로 대출 잔액을 조회합니다.
        return apiClient.get("/api/loan-accounts/" + loanId + "/balance", new TypeReference<BigDecimal>() {});
    }

    /**
     * 대출 상환을 위해 서버에 요청합니다.
     * @param loanId 상환할 대출의 고유 ID
     * @param repaymentAmount 상환 금액
     * @return 서버로부터 받은 업데이트된 LoanAccount 객체
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public LoanAccountResponseDto makeRepayment(String loanId, BigDecimal repaymentAmount) throws Exception {
        // POST 요청을 통해 /api/loan-accounts/{loanId}/repayment 엔드포인트로 상환 요청을 전송합니다.
        // @RequestParam은 URL 쿼리 파라미터로 전송되므로, 직접 URL에 추가합니다.
        return apiClient.post("/api/loan-accounts/" + loanId + "/repayment?repaymentAmount=" + repaymentAmount, null, new TypeReference<LoanAccountResponseDto>() {});
    }



    /**
     * 고객의 총 대출 잔액을 서버로부터 조회합니다.
     * @param customerId 조회할 고객의 고유 ID
     * @return 서버로부터 받은 총 대출 잔액 (BigDecimal)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public BigDecimal getTotalLoanBalanceByCustomerId(String customerId) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/customer/{customerId}/total-balance 엔드포인트로 총 대출 잔액을 조회합니다.
        return apiClient.get("/api/loan-accounts/customer/" + customerId + "/total-balance", new TypeReference<BigDecimal>() {});
    }

    /**
     * 고객의 대출 개수를 서버로부터 조회합니다.
     * @param customerId 조회할 고객의 고유 ID
     * @return 서버로부터 받은 활성 대출 개수 (String)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public String getActiveLoanCountByCustomerId(String customerId) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/customer/{customerId}/count 엔드포인트로 활성 대출 개수를 조회합니다.
        return apiClient.get("/api/loan-accounts/customer/" + customerId + "/count", new TypeReference<String>() {});
    }

    /**
     * 만기 임박 대출 목록을 서버로부터 조회합니다.
     * @param date 만기 임박 기준 날짜
     * @return 서버로부터 받은 LoanAccount 객체 목록
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public List<LoanAccountResponseDto> getLoansNearingMaturity(LocalDate date) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/near-maturity 엔드포인트로 만기 임박 대출을 조회합니다.
        // @RequestParam은 URL 쿼리 파라미터로 전송되므로, 직접 URL에 추가합니다.
        return apiClient.get("/api/loan-accounts/near-maturity?date=" + date.toString(), new TypeReference<List<LoanAccountResponseDto>>() {});
    }

    /**
     * 상품 이름으로 대출 계좌 목록을 서버로부터 조회합니다.
     * @param productName 조회할 상품 이름
     * @return 서버로부터 받은 LoanAccount 객체 목록
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public List<LoanAccountResponseDto> getLoanAccountsByProductName(String productName) throws Exception {
        // GET 요청을 통해 /api/loan-accounts/product/{productName} 엔드포인트로 상품별 대출 계좌를 조회합니다.
        return apiClient.get("/api/loan-accounts/product/" + productName, new TypeReference<List<LoanAccountResponseDto>>() {});
    }

}
