package org.example.kdt_bank_client2.DtoUser;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// API 공통 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseUser<T> {

    private Boolean success;
    private String message;
    private T data;
    private String errorCode;

    // 성공 응답
    public static <T> ApiResponseUser<T> success(T data) {
        ApiResponseUser<T> response = new ApiResponseUser<>(true, "성공", data, null);
        System.out.println("ApiResponseUser 성공 응답 생성: " + "데이터타입: " +
                (data != null ? data.getClass().getSimpleName() : "null"));
        return response;
    }

    public static <T> ApiResponseUser<T> success(String message, T data) {
        ApiResponseUser<T> response = new ApiResponseUser<>(true, message, data, null);
        System.out.println("ApiResponseUser 성공 응답 생성: " + message +
                ", 데이터타입: " + (data != null ? data.getClass().getSimpleName() : "null"));
        return response;
    }

    // 실패 응답
    public static <T> ApiResponseUser<T> error(String message) {
        ApiResponseUser<T> response = new ApiResponseUser<>(false, message, null, null);
        System.out.println("ApiResponseUser 에러 응답 생성: " + message);
        return response;
    }

    public static <T> ApiResponseUser<T> error(String message, String errorCode) {
        ApiResponseUser<T> response = new ApiResponseUser<>(false, message, null, errorCode);
        System.out.println("ApiResponseUser 에러 응답 생성: " + message +
                ", 에러코드: " + errorCode);
        return response;
    }
}
