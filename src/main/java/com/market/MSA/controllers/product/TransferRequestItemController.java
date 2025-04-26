package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.TransferRequestItem;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.TransferResponseItem;
import com.market.MSA.services.product.TransferRequestItemService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

  @GetMapping
  public ApiResponse<List<TransferResponseItem>> getAllTransferRequestItems(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<TransferResponseItem>>builder()
        .result(transferRequestItemService.getAllTransferRequestItems(page, pageSize))
        .message(ApiMessage.ALL_TRANSFER_REQUEST_ITEMS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/product/{productId}")
  public ApiResponse<List<TransferResponseItem>> getTransferRequestItemsByProductId(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @PathVariable Long productId) {
    return ApiResponse.<List<TransferResponseItem>>builder()
        .result(
            transferRequestItemService.getTransferRequestItemsByProductId(
                productId, page, pageSize))
        .message(ApiMessage.ALL_TRANSFER_REQUEST_ITEMS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/transfer/{transferId}")
  public ApiResponse<List<TransferResponseItem>> getTransferRequestItemsByTransferId(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @PathVariable Long transferId) {
    return ApiResponse.<List<TransferResponseItem>>builder()
        .result(
            transferRequestItemService.getTransferRequestItemsByTransferRequestId(
                transferId, page, pageSize))
        .message(ApiMessage.ALL_TRANSFER_REQUEST_ITEMS_RETRIEVED.getMessage())
        .build();
  }
}
