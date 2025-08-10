package org.example.kdt_bank_client2.DtoUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 상품 응답 DTO
@Data
@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDto {

    private String productId;
    private String productName;
    private String productDetail;
    private String category;
    private String productCategory;
    private BigDecimal maxRate;
    private BigDecimal minRate;
    private BigDecimal limitMoney;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
