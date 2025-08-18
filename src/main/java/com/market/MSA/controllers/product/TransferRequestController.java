package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.TransferRequestFilterRequest;
import com.market.MSA.requests.product.TransferRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.TransferResponse;
import com.market.MSA.services.product.TransferRequestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tr")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferRequestController {
  TransferRequestService transferRequestService;

  @PostMapping
  public ApiResponse<TransferResponse> createTransferRequest(@RequestBody TransferRequest request) {
    return ApiResponse.<TransferResponse>builder()
        .result(transferRequestService.createTransferRequest(request))
        .message(ApiMessage.TRANSFER_REQUEST_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<TransferResponse> updateTransferRequest(
      @PathVariable Long id, @RequestBody TransferRequest request) {
    return ApiResponse.<TransferResponse>builder()
        .result(transferRequestService.updateTransferRequest(id, request))
        .message(ApiMessage.TRANSFER_REQUEST_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteTransferRequest(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(transferRequestService.deleteTransferRequest(id))
        .message(ApiMessage.TRANSFER_REQUEST_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<TransferResponse> getTransferRequestById(@PathVariable Long id) {
    return ApiResponse.<TransferResponse>builder()
        .result(transferRequestService.getTransferRequestById(id))
        .message(ApiMessage.TRANSFER_REQUEST_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<TransferResponse>> getAll() {
    return ApiResponse.<List<TransferResponse>>builder()
        .result(transferRequestService.getAll())
        .message(ApiMessage.ALL_TRANSFER_REQUESTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<TransferResponse>> filterTransferRequests(
      @Valid @RequestBody TransferRequestFilterRequest request) {
    return ApiResponse.<List<TransferResponse>>builder()
        .result(transferRequestService.getAllTransferRequests(request))
        .message(ApiMessage.ALL_TRANSFER_REQUESTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<TransferResponse>> filterTransferRequestsWithPaging(
      @Valid @RequestBody TransferRequestFilterRequest request) {
    return ApiResponse.<Page<TransferResponse>>builder()
        .result(transferRequestService.getAllTransferRequestsWithPaging(request))
        .message(ApiMessage.ALL_TRANSFER_REQUESTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/{id}/approve")
  public ApiResponse<TransferResponse> approveTransferRequest(@PathVariable Long id) {
    return ApiResponse.<TransferResponse>builder()
        .result(transferRequestService.approveTransferRequest(id))
        .message(ApiMessage.TRANSFER_REQUEST_APPROVED.getMessage())
        .build();
  }

  @PostMapping("/{id}/reject")
  public ApiResponse<TransferResponse> rejectTransferRequest(
      @PathVariable Long id, @RequestParam String note) {
    return ApiResponse.<TransferResponse>builder()
        .result(transferRequestService.rejectTransferRequest(id, note))
        .message(ApiMessage.TRANSFER_REQUEST_REJECTED.getMessage())
        .build();
  }
}
