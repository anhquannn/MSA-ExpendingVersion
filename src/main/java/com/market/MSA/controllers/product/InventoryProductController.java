package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.InventoryProductRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.InventoryProductResponse;
import com.market.MSA.responses.product.InventoryStatisticsResponse;
import com.market.MSA.services.product.InventoryProductService;
import jakarta.validation.Valid;
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
      @RequestBody @Valid InventoryProductRequest request) {
    return ApiResponse.<InventoryProductResponse>builder()
        .result(inventoryProductService.createInventoryProduct(request))
        .message(ApiMessage.INVENTORY_PRODUCT_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<InventoryProductResponse> updateInventoryProduct(
      @PathVariable Long id, @RequestBody @Valid InventoryProductRequest request) {
    return ApiResponse.<InventoryProductResponse>builder()
        .result(inventoryProductService.updateInventoryProduct(id, request))
        .message(ApiMessage.INVENTORY_PRODUCT_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteInventoryProduct(@PathVariable Long id) {
    Boolean result = inventoryProductService.deleteInventoryProduct(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.INVENTORY_PRODUCT_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InventoryProductResponse> getInventoryProductById(@PathVariable Long id) {
    return ApiResponse.<InventoryProductResponse>builder()
        .result(inventoryProductService.getInventoryProductById(id))
        .message(ApiMessage.INVENTORY_PRODUCT_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/inventory/{inventoryId}")
  public ApiResponse<List<InventoryProductResponse>> getInventoryProductByInventoryId(
      @PathVariable long inventoryId, int page, int pageSize) {
    return ApiResponse.<List<InventoryProductResponse>>builder()
        .result(
            inventoryProductService.getInventoryProductByInventoryId(inventoryId, page, pageSize))
        .message(ApiMessage.ALL_INVENTORY_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/product/{productId}")
  public ApiResponse<List<InventoryProductResponse>> getInventoryProductByProductId(
      @PathVariable long productId) {
    return ApiResponse.<List<InventoryProductResponse>>builder()
        .result(inventoryProductService.getInventoryProductByProductId(productId))
        .message(ApiMessage.ALL_INVENTORY_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/statistic/{branchId}")
  public ApiResponse<InventoryStatisticsResponse> getInventoryStatistics(
      @PathVariable long branchId) {
    return ApiResponse.<InventoryStatisticsResponse>builder()
        .result(inventoryProductService.getInventoryStatistics(branchId))
        .message(ApiMessage.INVENTORY_PRODUCT_STATISTICS_CREATED.getMessage())
        .build();
  }

  @GetMapping("/branch/{branchId}/product/{productId}/stock")
  public ApiResponse<Integer> getTotalStockInBranch(
      @PathVariable Long branchId, @PathVariable Long productId) {
    return ApiResponse.<Integer>builder()
        .result(inventoryProductService.getTotalStockInBranch(branchId, productId))
        .message(ApiMessage.INVENTORY_PRODUCT_TOTAL_STOCK_CREATED.getMessage())
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
        .message(ApiMessage.ALL_INVENTORY_PRODUCTS_RETRIEVED.getMessage())
        .build();
  }
}
