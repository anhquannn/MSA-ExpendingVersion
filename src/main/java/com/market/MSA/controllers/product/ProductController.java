package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.ProductRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.ProductResponse;
import com.market.MSA.services.product.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
  ProductService productService;

  @PostMapping
  public ApiResponse<ProductResponse> createProduct(
      @RequestBody @Valid ProductRequest request,
      @RequestParam(defaultValue = "false") boolean sendNotificationToAll) {
    return ApiResponse.<ProductResponse>builder()
        .result(productService.createProduct(request, sendNotificationToAll))
        .message(ApiMessage.PRODUCT_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{productId}")
  public ApiResponse<ProductResponse> updateProduct(
      @PathVariable long productId, @RequestBody @Valid ProductRequest request) {
    return ApiResponse.<ProductResponse>builder()
        .result(productService.updateProduct(productId, request))
        .message(ApiMessage.PRODUCT_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{productId}")
  public ApiResponse<Boolean> deleteProduct(@PathVariable long productId) {
    Boolean result = productService.deleteProduct(productId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.PRODUCT_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{productId}")
  public ApiResponse<ProductResponse> getProductById(@PathVariable long productId) {
    return ApiResponse.<ProductResponse>builder()
        .result(productService.getProductById(productId))
        .message(ApiMessage.PRODUCT_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<ProductResponse>> getAllProducts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<ProductResponse>>builder()
        .result(productService.getAllProducts(page, pageSize))
        .message(ApiMessage.ALL_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/filter")
  public ApiResponse<List<ProductResponse>> filterAndSortProducts(
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String color,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<ProductResponse>>builder()
        .result(
            productService.filterAndSortProducts(
                minPrice, maxPrice, color, categoryId, page, pageSize))
        .message(ApiMessage.ALL_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/branch/{branchId}")
  public ApiResponse<Page<ProductResponse>> getAllProductsInBranch(
      @PathVariable Long branchId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection) {
    return ApiResponse.<Page<ProductResponse>>builder()
        .result(productService.getAllProductsInBranch(branchId, page, size, sortBy, sortDirection))
        .message(ApiMessage.ALL_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/branch/{branchId}/search")
  public ApiResponse<Page<ProductResponse>> searchProductsInBranch(
      @PathVariable Long branchId,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String color,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection) {
    return ApiResponse.<Page<ProductResponse>>builder()
        .result(
            productService.searchProductsInBranch(
                branchId,
                keyword,
                minPrice,
                maxPrice,
                color,
                page,
                pageSize,
                sortBy,
                sortDirection))
        .message(ApiMessage.ALL_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/search")
  public ApiResponse<Page<ProductResponse>> searchProducts(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String color,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long manufacturerId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection) {
    return ApiResponse.<Page<ProductResponse>>builder()
        .result(
            productService.searchProducts(
                keyword,
                minPrice,
                maxPrice,
                color,
                categoryId,
                manufacturerId,
                page,
                pageSize,
                sortBy,
                sortDirection))
        .message(ApiMessage.ALL_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }
}
