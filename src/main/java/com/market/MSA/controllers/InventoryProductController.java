package com.market.MSA.controllers;

import com.market.MSA.requests.InventoryProductRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.InventoryProductResponse;
import com.market.MSA.responses.InventoryStatisticsResponse;
import com.market.MSA.services.InventoryProductService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryProductController {
  InventoryProductService inventoryProductService;

  @PostMapping
  public ApiResponse<InventoryProductResponse> createInventoryProduct(
      @RequestBody InventoryProductRequest request) {
    return ApiResponse.<InventoryProductResponse>builder()
        .result(inventoryProductService.createInventoryProduct(request))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<InventoryProductResponse> updateInventoryProduct(
      @PathVariable long id, @RequestBody InventoryProductRequest request) {
    return ApiResponse.<InventoryProductResponse>builder()
        .result(inventoryProductService.updateInventoryProduct(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteInventoryProduct(@PathVariable long id) {
    return ApiResponse.<Boolean>builder()
        .result(inventoryProductService.deleteInventoryProduct(id))
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InventoryProductResponse> getInventoryProductById(@PathVariable long id) {
    return ApiResponse.<InventoryProductResponse>builder()
        .result(inventoryProductService.getInventoryProductById(id))
        .build();
  }

  @GetMapping("/inventory/{inventoryId}")
  public ApiResponse<List<InventoryProductResponse>> getInventoryProductByInventoryId(
      @PathVariable long inventoryId, int page, int pageSize) {
    return ApiResponse.<List<InventoryProductResponse>>builder()
        .result(
            inventoryProductService.getInventoryProductByInventoryId(inventoryId, page, pageSize))
        .build();
  }

  @GetMapping("/product/{productId}")
  public ApiResponse<List<InventoryProductResponse>> getInventoryProductByProductId(
      @PathVariable long productId) {
    return ApiResponse.<List<InventoryProductResponse>>builder()
        .result(inventoryProductService.getInventoryProductByProductId(productId))
        .build();
  }

  @GetMapping("/statistic/{branchId}")
  public ApiResponse<InventoryStatisticsResponse> getInventoryStatistics(
      @PathVariable long branchId) {
    return ApiResponse.<InventoryStatisticsResponse>builder()
        .result(inventoryProductService.getInventoryStatistics(branchId))
        .build();
  }

  @GetMapping("/branch/{branchId}/product/{productId}/stock")
  public ApiResponse<Integer> getTotalStockInBranch(
      @PathVariable Long branchId, @PathVariable Long productId) {
    return ApiResponse.<Integer>builder()
        .result(inventoryProductService.getTotalStockInBranch(branchId, productId))
        .build();
  }

  @GetMapping("/branch/{branchId}/products")
  public ApiResponse<Page<InventoryProductResponse>> getInventoryProductsByBranch(
      @PathVariable Long branchId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "stockNumber") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    return ApiResponse.<Page<InventoryProductResponse>>builder()
        .result(
            inventoryProductService.getInventoryProductsByBranch(
                branchId, page, size, sortBy, sortDirection))
        .build();
  }
}
