package com.example.KDT_bank_server_project2.manager.ControllerUser;


import com.example.KDT_bank_server_project2.manager.EntityUser.Product;
import com.example.KDT_bank_server_project2.manager.ServiceUser.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 상품 생성
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.ok(createdProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 모든 상품 조회
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 활성 상품만 조회
    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    // ID로 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 상품명으로 상품 조회
    @GetMapping("/name/{productName}")
    public ResponseEntity<Product> getProductByName(@PathVariable String productName) {
        Optional<Product> product = productService.getProductByName(productName);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 상품 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 상품 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<Product> updateProductStatus(@PathVariable Long id,
                                                       @RequestParam Product.ProductStatus status) {
        try {
            Product updatedProduct = productService.updateProductStatus(id, status);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 카테고리별 상품 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // 상품 카테고리별 조회
    @GetMapping("/product-category/{productCategory}")
    public ResponseEntity<List<Product>> getProductsByProductCategory(@PathVariable String productCategory) {
        List<Product> products = productService.getProductsByProductCategory(productCategory);
        return ResponseEntity.ok(products);
    }

    // 금리 범위로 상품 검색
    @GetMapping("/interest-rate")
    public ResponseEntity<List<Product>> getProductsByInterestRateRange(
            @RequestParam BigDecimal minRate,
            @RequestParam BigDecimal maxRate) {
        List<Product> products = productService.getProductsByInterestRateRange(minRate, maxRate);
        return ResponseEntity.ok(products);
    }

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    // 카테고리별 최고금리순 상품 조회
    @GetMapping("/category/{category}/by-rate")
    public ResponseEntity<List<Product>> getProductsByCategoryOrderByMaxRate(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategoryOrderByMaxRate(category);
        return ResponseEntity.ok(products);
    }

    // 상품 비활성화
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        try {
            productService.deactivateProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
