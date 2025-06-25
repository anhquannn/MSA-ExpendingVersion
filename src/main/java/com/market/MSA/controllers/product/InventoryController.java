package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.InventoryFilterRequest;
import com.market.MSA.requests.product.InventoryRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.InventoryResponse;
import com.market.MSA.services.product.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
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
        .message(ApiMessage.INVENTORY_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<InventoryResponse> updateInventory(
      @PathVariable long id, @RequestBody InventoryRequest inventoryRequest) {
    return ApiResponse.<InventoryResponse>builder()
        .result(inventoryService.updateInventory(id, inventoryRequest))
        .message(ApiMessage.INVENTORY_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteInventory(@PathVariable long id) {
    return ApiResponse.<Boolean>builder()
        .result(inventoryService.deleteInventory(id))
        .message(ApiMessage.INVENTORY_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InventoryResponse> getInventoryById(@PathVariable long id) {
    return ApiResponse.<InventoryResponse>builder()
        .result(inventoryService.getInventoryById(id))
        .message(ApiMessage.INVENTORY_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<InventoryResponse>> filterInventories(
      @Valid @RequestBody InventoryFilterRequest request) {
    return ApiResponse.<List<InventoryResponse>>builder()
        .result(inventoryService.getAllInventories(request))
        .message(ApiMessage.ALL_INVENTORIES_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<InventoryResponse>> getAll() {
    return ApiResponse.<List<InventoryResponse>>builder()
        .result(inventoryService.getAll())
        .message(ApiMessage.ALL_INVENTORIES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<InventoryResponse>> filterInventoriesWithPaging(
      @Valid @RequestBody InventoryFilterRequest request) {
    return ApiResponse.<Page<InventoryResponse>>builder()
        .result(inventoryService.getAllInventoriesWithPaging(request))
        .message(ApiMessage.ALL_INVENTORIES_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/branch/{branchId}")
  public ApiResponse<InventoryResponse> getInventoryByBranchId(@PathVariable long branchId) {
    return ApiResponse.<InventoryResponse>builder()
        .result(inventoryService.getInventoryByBranchId(branchId))
        .message(ApiMessage.INVENTORY_RETRIEVED.getMessage())
        .build();
  }
}
