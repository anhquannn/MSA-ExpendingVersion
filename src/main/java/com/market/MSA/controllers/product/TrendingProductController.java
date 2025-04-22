package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.TrendingProductRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.TrendingProductResponse;
import com.market.MSA.services.product.TrendingProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trending-product")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrendingProductController {
  TrendingProductService trendingProductService;

  // Tạo TrendingProduct
  @PostMapping
  public ApiResponse<TrendingProductResponse> createTrendingProduct(
      @RequestBody @Valid TrendingProductRequest request) {
    log.info("Received request to create trending product: {}", request);
    return ApiResponse.<TrendingProductResponse>builder()
        .result(trendingProductService.createTrendingProduct(request))
        .message(ApiMessage.TRENDING_PRODUCT_CREATED.getMessage())
        .build();
  }

  // Cập nhật TrendingProduct
  @PutMapping("/{trendingProductId}")
  public ApiResponse<TrendingProductResponse> updateTrendingProduct(
      @PathVariable long trendingProductId, @RequestBody @Valid TrendingProductRequest request) {
    log.info("Updating trending product with ID: {}", trendingProductId);
    return ApiResponse.<TrendingProductResponse>builder()
        .result(trendingProductService.updateTrendingProduct(trendingProductId, request))
        .message(ApiMessage.TRENDING_PRODUCT_UPDATED.getMessage())
        .build();
  }

  // Xóa TrendingProduct
  @DeleteMapping("/{trendingProductId}")
  public ApiResponse<Boolean> deleteTrendingProduct(@PathVariable long trendingProductId) {
    Boolean result = trendingProductService.deleteTrendingProduct(trendingProductId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.TRENDING_PRODUCT_DELETED.getMessage())
        .build();
  }

  // Lấy TrendingProduct theo ID
  @GetMapping("/{trendingProductId}")
  public ApiResponse<TrendingProductResponse> getTrendingProductById(
      @PathVariable long trendingProductId) {
    log.info("Fetching trending product with ID: {}", trendingProductId);
    return ApiResponse.<TrendingProductResponse>builder()
        .result(trendingProductService.getTrendingProductById(trendingProductId))
        .message(ApiMessage.TRENDING_PRODUCT_RETRIEVED.getMessage())
        .build();
  }

  // Lấy tất cả TrendingProduct (phân trang)
  @GetMapping
  public ApiResponse<List<TrendingProductResponse>> getAllTrendingProducts(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    log.info("Fetching all trending products, page: {}, pageSize: {}", page, pageSize);
    return ApiResponse.<List<TrendingProductResponse>>builder()
        .result(trendingProductService.getAllTrendingProducts(page, pageSize))
        .message(ApiMessage.ALL_TRENDING_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }
}
