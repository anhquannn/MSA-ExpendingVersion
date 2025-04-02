package com.market.MSA.controllers;

import com.market.MSA.requests.StockTransferRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.StockTransferResponse;
import com.market.MSA.services.StockTransferService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock-transfer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransferController {
  StockTransferService stockTransferService;

  @PostMapping
  public ApiResponse<StockTransferResponse> createTransferStock(
      @RequestBody StockTransferRequest request) {
    return ApiResponse.<StockTransferResponse>builder()
        .result(stockTransferService.createTransferStock(request))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<StockTransferResponse> updateTransferStock(
      @PathVariable long id, @RequestBody StockTransferRequest request) {
    return ApiResponse.<StockTransferResponse>builder()
        .result(stockTransferService.updateStock(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteTransferStock(@PathVariable long id) {
    return ApiResponse.<Boolean>builder().result(stockTransferService.deleteStock(id)).build();
  }

  @GetMapping("/{id}")
  public ApiResponse<StockTransferResponse> getTransferStockById(@PathVariable long id) {
    return ApiResponse.<StockTransferResponse>builder()
        .result(stockTransferService.getStockTransferById(id))
        .build();
  }

  @GetMapping("/{fromBranchId}")
  public ApiResponse<List<StockTransferResponse>> getTransferStockByFromBranchId(
      @PathVariable long fromBranchId, @RequestParam String status) {
    return ApiResponse.<List<StockTransferResponse>>builder()
        .result(stockTransferService.getStockTransferByFromBranchId(fromBranchId, status))
        .build();
  }

  @GetMapping("/{toBranchId}")
  public ApiResponse<List<StockTransferResponse>> getTransferStockByToBranchId(
      @PathVariable long toBranchId, @RequestParam String status) {
    return ApiResponse.<List<StockTransferResponse>>builder()
        .result(stockTransferService.getStockTransferByToBranchId(toBranchId, status))
        .build();
  }

  @GetMapping("/{userRequestId}")
  public ApiResponse<List<StockTransferResponse>> getTransferStockByUserRequestId(
      @PathVariable long userRequestId, @RequestParam String status) {
    return ApiResponse.<List<StockTransferResponse>>builder()
        .result(stockTransferService.getStockTransferByUserRequestId(userRequestId, status))
        .build();
  }

  @GetMapping("/{userResponseId}")
  public ApiResponse<List<StockTransferResponse>> getTransferStockByUserResponseId(
      @PathVariable long userResponseId, @RequestParam String status) {
    return ApiResponse.<List<StockTransferResponse>>builder()
        .result(stockTransferService.getStockTransferByUserResponseId(userResponseId, status))
        .build();
  }

  @GetMapping("/{productId}")
  public ApiResponse<List<StockTransferResponse>> getTransferStockByProductId(
      @PathVariable long productId, @RequestParam String status) {
    return ApiResponse.<List<StockTransferResponse>>builder()
        .result(stockTransferService.getStockTransferByProductId(productId, status))
        .build();
  }
}
