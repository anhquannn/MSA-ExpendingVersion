package com.market.MSA.controllers;

import com.market.MSA.requests.InventoryRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.InventoryResponse;
import com.market.MSA.services.InventoryService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryController {
  InventoryService inventoryService;

  @PostMapping
  public ApiResponse<InventoryResponse> createInventory(
      @RequestBody InventoryRequest inventoryRequest) {
    return ApiResponse.<InventoryResponse>builder()
        .result(inventoryService.createInventory(inventoryRequest))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<InventoryResponse> updateInventory(
      @PathVariable long id, @RequestBody InventoryRequest inventoryRequest) {
    return ApiResponse.<InventoryResponse>builder()
        .result(inventoryService.updateInventory(id, inventoryRequest))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteInventory(@PathVariable long id) {
    return ApiResponse.<Boolean>builder().result(inventoryService.deleteInventory(id)).build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InventoryResponse> getInventoryById(@PathVariable long id) {
    return ApiResponse.<InventoryResponse>builder()
        .result(inventoryService.getInventoryById(id))
        .build();
  }

  @GetMapping
  public ApiResponse<List<InventoryResponse>> getAllInventory(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<InventoryResponse>>builder()
        .result(inventoryService.getAllInventory(page, pageSize))
        .build();
  }

  @GetMapping("/branch/{branchId}")
  public ApiResponse<List<InventoryResponse>> getInventoryByBranchId(@PathVariable long branchId) {
    return ApiResponse.<List<InventoryResponse>>builder()
        .result(inventoryService.getInventoryByBranchId(branchId))
        .build();
  }

  @GetMapping("/search")
  public ApiResponse<List<InventoryResponse>> searchInventories(
      @RequestParam String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<InventoryResponse>>builder()
        .result(inventoryService.searchInventoryByKeyword(name, page, pageSize))
        .build();
  }
}
