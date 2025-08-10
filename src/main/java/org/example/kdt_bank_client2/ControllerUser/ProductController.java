package org.example.kdt_bank_client2.ControllerUser;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.ApiClient;
import org.example.kdt_bank_client2.ApiResponse;
import org.example.kdt_bank_client2.DtoUser.ProductCreateRequestDto;
import org.example.kdt_bank_client2.DtoUser.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductController {

    private final ApiClient api;

    // POST /api/products
    // 상품 생성
    public ApiResponse<ProductResponseDto> createProduct(ProductCreateRequestDto dto) throws Exception {
        return api.post("/api/products",
                dto,
                new TypeReference<ApiResponse<ProductResponseDto>>() {});
    }

    // GET /api/products
    //모든 상품 조회
    public ApiResponse<List<ProductResponseDto>> getAllProducts() throws Exception {
        return api.get("/api/products",
                new TypeReference<ApiResponse<List<ProductResponseDto>>>() {});
    }

    // GET /api/products/active
    public ApiResponse<List<ProductResponseDto>> getActiveProducts() throws Exception {
        return api.get("/api/products/active",
                new TypeReference<ApiResponse<List<ProductResponseDto>>>() {});
    }

    // GET /api/products/{id}
    public ApiResponse<ProductResponseDto> getProductById(Long id) throws Exception {
        return api.get("/api/products/" + id,
                new TypeReference<ApiResponse<ProductResponseDto>>() {});
    }

    // GET /api/products/category/{category}
    public ApiResponse<List<ProductResponseDto>> getProductsByCategory(String category) throws Exception {
        return api.get("/api/products/category/" + category,
                new TypeReference<ApiResponse<List<ProductResponseDto>>>() {});
    }

    // GET /api/products/search?keyword=...
    public ApiResponse<List<ProductResponseDto>> searchProducts(String keyword) throws Exception {
        return api.get("/api/products/search?keyword=" + keyword,
                new TypeReference<ApiResponse<List<ProductResponseDto>>>() {});
    }
}
