package com.market.MSA.controllers;

import com.market.MSA.requests.TrendingProductRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.TrendingProductResponse;
import com.market.MSA.services.TrendingProductService;
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
        .build();
  }

  // Cập nhật TrendingProduct
  @PutMapping("/{trendingProductId}")
  public ApiResponse<TrendingProductResponse> updateTrendingProduct(
      @PathVariable long trendingProductId, @RequestBody @Valid TrendingProductRequest request) {
    log.info("Updating trending product with ID: {}", trendingProductId);
    return ApiResponse.<TrendingProductResponse>builder()
        .result(trendingProductService.updateTrendingProduct(trendingProductId, request))
        .build();
  }

  // Xóa TrendingProduct
  @DeleteMapping("/{trendingProductId}")
  public ApiResponse<String> deleteTrendingProduct(@PathVariable long trendingProductId) {
    log.info("Deleting trending product with ID: {}", trendingProductId);
    trendingProductService.deleteTrendingProduct(trendingProductId);
    return ApiResponse.<String>builder().result("Trending product has been deleted").build();
  }

  // Lấy TrendingProduct theo ID
  @GetMapping("/{trendingProductId}")
  public ApiResponse<TrendingProductResponse> getTrendingProductById(
      @PathVariable long trendingProductId) {
    log.info("Fetching trending product with ID: {}", trendingProductId);
    return ApiResponse.<TrendingProductResponse>builder()
        .result(trendingProductService.getTrendingProductById(trendingProductId))
        .build();
  }

  // Lấy tất cả TrendingProduct (phân trang)
  @GetMapping
  public ApiResponse<List<TrendingProductResponse>> getAllTrendingProducts(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    log.info("Fetching all trending products, page: {}, pageSize: {}", page, pageSize);
    return ApiResponse.<List<TrendingProductResponse>>builder()
        .result(trendingProductService.getAllTrendingProducts(page, pageSize))
        .build();
  }
}
