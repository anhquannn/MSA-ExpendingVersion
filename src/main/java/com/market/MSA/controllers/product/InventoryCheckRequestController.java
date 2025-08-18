package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.InventoryCheckRequestFilterRequest;
import com.market.MSA.requests.product.InventoryCheckRequestRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.InventoryCheckRequestResponse;
import com.market.MSA.services.product.InventoryCheckRequestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-check-requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryCheckRequestController {

  InventoryCheckRequestService icrService;

  @PreAuthorize("hasRole('SURVEYOR')")
  @PostMapping
  public ApiResponse<InventoryCheckRequestResponse> create(
      @RequestBody InventoryCheckRequestRequest req) {
    return ApiResponse.<InventoryCheckRequestResponse>builder()
        .result(icrService.create(req))
        .message(ApiMessage.INVENTORY_CHECK_REQUEST_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<InventoryCheckRequestResponse> update(
      @PathVariable Long id, @RequestBody InventoryCheckRequestRequest req) {
    return ApiResponse.<InventoryCheckRequestResponse>builder()
        .result(icrService.update(id, req))
        .message(ApiMessage.INVENTORY_CHECK_REQUEST_UPDATED.getMessage())
        .build();
  }

  @PutMapping("/{id}/received")
  public ApiResponse<InventoryCheckRequestResponse> updateStatus(@PathVariable Long id) {
    return ApiResponse.<InventoryCheckRequestResponse>builder()
        .result(icrService.updateStatusToReceived(id))
        .message(ApiMessage.INVENTORY_CHECK_REQUEST_STATUS_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> delete(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(icrService.delete(id))
        .message(ApiMessage.INVENTORY_CHECK_REQUEST_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InventoryCheckRequestResponse> getById(@PathVariable Long id) {
    return ApiResponse.<InventoryCheckRequestResponse>builder()
        .result(icrService.getById(id))
        .message(ApiMessage.INVENTORY_CHECK_REQUEST_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<InventoryCheckRequestResponse>> filter(
      @Valid @RequestBody InventoryCheckRequestFilterRequest req) {
    return ApiResponse.<List<InventoryCheckRequestResponse>>builder()
        .result(icrService.filter(req))
        .message(ApiMessage.ALL_INVENTORY_CHECK_REQUESTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<InventoryCheckRequestResponse>> filterPaging(
      @Valid @RequestBody InventoryCheckRequestFilterRequest req) {
    return ApiResponse.<Page<InventoryCheckRequestResponse>>builder()
        .result(icrService.filterPaging(req))
        .message(ApiMessage.ALL_INVENTORY_CHECK_REQUESTS_RETRIEVED.getMessage())
        .build();
  }
}
