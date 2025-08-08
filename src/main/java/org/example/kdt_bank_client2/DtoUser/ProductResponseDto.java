package org.example.kdt_bank_client2.DtoUser;



import com.example.KDT_bank_server_project2.manager.EntityUser.Product;
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

    private Long productId;
    private String productName;
    private String productDetail;
    private String category;
    private String productCategory;
    private BigDecimal maxRate;
    private BigDecimal minRate;
    private BigDecimal limitMoney;
    private Product.ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponseDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.productDetail = product.getProductDetail();
        this.category = product.getCategory();
        this.productCategory = product.getProductCategory();
        this.maxRate = product.getMaxRate();
        this.minRate = product.getMinRate();
        this.limitMoney = product.getLimitMoney();
        this.status = product.getStatus();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        System.out.println("ProductResponseDto 생성: " + productName +
                ", category: " + category +
                ", status: " + status);
    }
}
