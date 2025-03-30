package com.market.MSA.controllers;

import com.market.MSA.requests.ReturnOrderRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.ReturnOrderResponse;
import com.market.MSA.services.ReturnOrderService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/return-order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnOrderController {
  ReturnOrderService returnOrderService;

  @PostMapping
  public ApiResponse<ReturnOrderResponse> createReturnOrder(
      @RequestBody ReturnOrderRequest request) {
    return ApiResponse.<ReturnOrderResponse>builder()
        .result(returnOrderService.createReturnOrder(request))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<ReturnOrderResponse> updateReturnOrder(
      @PathVariable Long id, @RequestBody ReturnOrderRequest request) {
    return ApiResponse.<ReturnOrderResponse>builder()
        .result(returnOrderService.updateReturnOrder(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<String> deleteReturnOrder(@PathVariable Long id) {
    returnOrderService.deleteReturnOrder(id);
    return ApiResponse.<String>builder().result("Success").build();
  }

  @GetMapping("/{id}")
  public ApiResponse<ReturnOrderResponse> getReturnOrderById(@PathVariable Long id) {
    return ApiResponse.<ReturnOrderResponse>builder()
        .result(returnOrderService.getReturnOrderById(id))
        .build();
  }

  @GetMapping
  public ApiResponse<List<ReturnOrderResponse>> getAllReturnOrders() {
    return ApiResponse.<List<ReturnOrderResponse>>builder()
        .result(returnOrderService.getAllReturnOrders())
        .build();
  }
}
