package org.example.kdt_bank_client2.ControllerUser;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.DtoUser.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 클라이언트 애플리케이션의 고객 관련 요청을 처리하는 컨트롤러입니다.
 * 이 컨트롤러는 직접 서버와 통신하지 않고, CustomerSession을 통해 비즈니스 로직을 위임합니다.
 * UI 또는 다른 클라이언트 컴포넌트에서 이 컨트롤러의 메서드를 호출하여 고객 관리 기능을 사용합니다.
 */
@Component
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final ApiClient apiClient;

    /**
     * 새로운 고객을 생성하기 위해 서버에 요청합니다.
     * @param requestDto 고객 생성에 필요한 데이터를 담은 DTO
     * @return 서버로부터 받은 고객 생성 결과 응답 (ApiResponseUser<CustomerResponseDto>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<CustomerResponseDto> createCustomer(CustomerCreateRequestDto requestDto) throws Exception {
        // POST 요청을 통해 /api/customers 엔드포인트로 requestDto를 전송합니다.
        return apiClient.post("/api/customers", requestDto, new TypeReference<ApiResponseUser<CustomerResponseDto>>() {});
    }

    public CustomerResponseDto getCustomerByNameAndResidentNumber(String name, String number) throws Exception {
        // POST 요청을 통해 /api/customers 엔드포인트로 requestDto를 전송합니다.
        ApiResponseUser<CustomerResponseDto> dto=apiClient.get("/api/customers/"+name+"/"+number, new TypeReference<ApiResponseUser<CustomerResponseDto>>() {});
        return dto.getData();

    }

    /**
     * 모든 고객의 요약 정보를 서버로부터 조회합니다.
     * @return 서버로부터 받은 고객 요약 정보 목록 응답 (ApiResponseUser<List<CustomerSummaryDto>>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<List<CustomerSummaryDto>> getAllCustomers() throws Exception {
        // GET 요청을 통해 /api/customers 엔드포인트로 모든 고객 요약 정보를 조회합니다.
        return apiClient.get("/api/customers", new TypeReference<ApiResponseUser<List<CustomerSummaryDto>>>() {});
    }

    /**
     * ID를 사용하여 특정 고객의 상세 정보를 서버로부터 조회합니다.
     * @param id 조회할 고객의 고유 ID
     * @return 서버로부터 받은 고객 상세 정보 응답 (ApiResponseUser<CustomerResponseDto>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<CustomerResponseDto> getCustomerById(String id) throws Exception {
        // GET 요청을 통해 /api/customers/{id} 엔드포인트로 고객 상세 정보를 조회합니다.
        return apiClient.get("/api/customers/" + id, new TypeReference<ApiResponseUser<CustomerResponseDto>>() {});
    }

    /**
     * 이메일을 사용하여 특정 고객의 상세 정보를 서버로부터 조회합니다.
     * @param email 조회할 고객의 이메일 주소
     * @return 서버로부터 받은 고객 상세 정보 응답 (ApiResponseUser<CustomerResponseDto>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<CustomerResponseDto> getCustomerByEmail(String email) throws Exception {
        // GET 요청을 통해 /api/customers/email/{email} 엔드포인트로 고객 상세 정보를 조회합니다.
        return apiClient.get("/api/customers/email/" + email, new TypeReference<ApiResponseUser<CustomerResponseDto>>() {});
    }

    /**
     * 고객 정보를 수정하기 위해 서버에 요청합니다.
     * @param id 수정할 고객의 고유 ID
     * @param requestDto 고객 수정에 필요한 데이터를 담은 DTO
     * @return 서버로부터 받은 고객 수정 결과 응답 (ApiResponseUser<CustomerResponseDto>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<CustomerResponseDto> updateCustomer(String id, CustomerUpdateRequestDto requestDto) throws Exception {
        // PUT 요청을 통해 /api/customers/{id} 엔드포인트로 requestDto를 전송하여 고객 정보를 수정합니다.
        // ApiClient에 put 메서드가 없으므로 post를 사용합니다. 실제 PUT 요청이 필요하면 ApiClient에 put 메서드를 추가해야 합니다.
        return apiClient.post("/api/customers/" + id, requestDto, new TypeReference<ApiResponseUser<CustomerResponseDto>>() {});
    }

    /**
     * 고객의 상태를 변경하기 위해 서버에 요청합니다.
     * @param id 상태를 변경할 고객의 고유 ID
     * @param status 변경할 고객 상태 (CustomerResponseDto.CustomerStatus enum)
     * @return 서버로부터 받은 고객 상태 변경 결과 응답 (ApiResponseUser<CustomerResponseDto>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<CustomerResponseDto> updateCustomerStatus(String id, String status) throws Exception {
        // PATCH 요청을 통해 /api/customers/{id}/status 엔드포인트로 고객 상태를 변경합니다.
        // ApiClient에 patch 메서드가 없으므로 post를 사용합니다. 실제 PATCH 요청이 필요하면 ApiClient에 patch 메서드를 추가해야 합니다.
        // @RequestParam은 URL 쿼리 파라미터로 전송되므로, 직접 URL에 추가합니다.
        return apiClient.post("/api/customers/" + id + "/status?status=" + status, null, new TypeReference<ApiResponseUser<CustomerResponseDto>>() {});
    }

    /**
     * 키워드를 사용하여 고객을 검색합니다.
     * @param keyword 검색할 키워드
     * @return 서버로부터 받은 검색 결과 고객 요약 정보 목록 응답 (ApiResponseUser<List<CustomerSummaryDto>>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<List<CustomerSummaryDto>> searchCustomers(String keyword) throws Exception {
        // GET 요청을 통해 /api/customers/search 엔드포인트로 키워드를 전송하여 고객을 검색합니다.
        return apiClient.get("/api/customers/search?keyword=" + keyword, new TypeReference<ApiResponseUser<List<CustomerSummaryDto>>>() {});
    }

    /**
     * 특정 상태(예: ACTIVE, INACTIVE)에 해당하는 고객 목록을 조회합니다.
     * @param status 조회할 고객 상태 (CustomerSummaryDto.CustomerStatus enum)
     * @return 서버로부터 받은 상태별 고객 요약 정보 목록 응답 (ApiResponseUser<List<CustomerSummaryDto>>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<List<CustomerSummaryDto>> getCustomersByStatus(String status) throws Exception {
        // GET 요청을 통해 /api/customers/status/{status} 엔드포인트로 상태별 고객을 조회합니다.
        return apiClient.get("/api/customers/status/" + status, new TypeReference<ApiResponseUser<List<CustomerSummaryDto>>>() {});
    }

    /**
     * 이메일 중복 여부를 확인하기 위해 서버에 요청합니다.
     * @param email 중복 확인을 할 이메일 주소
     * @return 서버로부터 받은 이메일 중복 여부 응답 (ApiResponseUser<Boolean>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<Boolean> checkEmailExists(String email) throws Exception {
        // GET 요청을 통해 /api/customers/check-email/{email} 엔드포인트로 이메일 중복 여부를 확인합니다.
        return apiClient.get("/api/customers/check-email/" + email, new TypeReference<ApiResponseUser<Boolean>>() {});
    }

    /**
     * 고객을 비활성화(삭제)하기 위해 서버에 요청합니다.
     * @param id 비활성화할 고객의 고유 ID
     * @return 서버로부터 받은 고객 비활성화 결과 응답 (ApiResponseUser<Void>)
     * @throws Exception API 호출 중 발생할 수 있는 예외
     */
    public ApiResponseUser<Void> deleteCustomer(String id) throws Exception {
        // DELETE 요청을 통해 /api/customers/{id} 엔드포인트로 고객을 비활성화합니다.
        return apiClient.get("/api/customers/" + id, new TypeReference<ApiResponseUser<Void>>() {});
    }
}