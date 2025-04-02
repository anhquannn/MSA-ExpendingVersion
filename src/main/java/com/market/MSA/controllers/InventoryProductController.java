package com.market.MSA.controllers;

import com.market.MSA.requests.InventoryProductRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.InventoryProductResponse;
import com.market.MSA.services.InventoryProductService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
      @PathVariable long inventoryId) {
    return ApiResponse.<List<InventoryProductResponse>>builder()
        .result(inventoryProductService.getInventoryProductByInventoryId(inventoryId))
        .build();
  }

  @GetMapping("/product/{productId}")
  public ApiResponse<List<InventoryProductResponse>> getInventoryProductByProductId(
      @PathVariable long productId) {
    return ApiResponse.<List<InventoryProductResponse>>builder()
        .result(inventoryProductService.getInventoryProductByProductId(productId))
        .build();
  }
}
