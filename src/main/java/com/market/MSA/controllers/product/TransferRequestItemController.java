package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.TransferRequestItemFilterRequest;
import com.market.MSA.requests.product.TransferRequestItem;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.TransferResponseItem;
import com.market.MSA.services.product.TransferRequestItemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tri")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferRequestItemController {
  TransferRequestItemService transferRequestItemService;

  @PostMapping
  public ApiResponse<TransferResponseItem> createTransferRequestItem(
      @RequestBody TransferRequestItem transferRequestItem) {
    return ApiResponse.<TransferResponseItem>builder()
        .result(transferRequestItemService.createTransferRequestItem(transferRequestItem))
        .message(ApiMessage.TRANSFER_REQUEST_ITEM_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<TransferResponseItem> updateTransferRequestItem(
      @PathVariable Long id, @RequestBody TransferRequestItem transferRequestItem) {
    return ApiResponse.<TransferResponseItem>builder()
        .result(transferRequestItemService.updateTransferRequestItem(id, transferRequestItem))
        .message(ApiMessage.TRANSFER_REQUEST_ITEM_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteTransferRequestItem(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(transferRequestItemService.deleteTransferRequestItem(id))
        .message(ApiMessage.TRANSFER_REQUEST_ITEM_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<TransferResponseItem> getTransferRequestItemById(@PathVariable Long id) {
    return ApiResponse.<TransferResponseItem>builder()
        .result(transferRequestItemService.getTransferRequestItemById(id))
        .message(ApiMessage.TRANSFER_REQUEST_ITEM_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<TransferResponseItem>> filterTransferRequestItems(
      @Valid @RequestBody TransferRequestItemFilterRequest request) {
    return ApiResponse.<List<TransferResponseItem>>builder()
        .result(transferRequestItemService.getAllTransferRequestItems(request))
        .message(ApiMessage.ALL_TRANSFER_REQUEST_ITEMS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<TransferResponseItem>> filterTransferRequestItemsWithPaging(
      @Valid @RequestBody TransferRequestItemFilterRequest request) {
    return ApiResponse.<Page<TransferResponseItem>>builder()
        .result(transferRequestItemService.getAllTransferRequestItemsWithPaging(request))
        .message(ApiMessage.ALL_TRANSFER_REQUEST_ITEMS_RETRIEVED.getMessage())
        .build();
  }
}
