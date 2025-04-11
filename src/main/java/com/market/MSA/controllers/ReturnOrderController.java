package com.market.MSA.controllers;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.ReturnOrderRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.ReturnOrderResponse;
import com.market.MSA.services.ReturnOrderService;
import jakarta.validation.Valid;
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
      @RequestBody @Valid ReturnOrderRequest request) {
    return ApiResponse.<ReturnOrderResponse>builder()
        .result(returnOrderService.createReturnOrder(request))
        .message(ApiMessage.RETURN_ORDER_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<ReturnOrderResponse> updateReturnOrder(
      @PathVariable Long id, @RequestBody @Valid ReturnOrderRequest request) {
    return ApiResponse.<ReturnOrderResponse>builder()
        .result(returnOrderService.updateReturnOrder(id, request))
        .message(ApiMessage.RETURN_ORDER_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteReturnOrder(@PathVariable Long id) {
    Boolean result = returnOrderService.deleteReturnOrder(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.RETURN_ORDER_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<ReturnOrderResponse> getReturnOrderById(@PathVariable Long id) {
    return ApiResponse.<ReturnOrderResponse>builder()
        .result(returnOrderService.getReturnOrderById(id))
        .message(ApiMessage.RETURN_ORDER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<ReturnOrderResponse>> getAllReturnOrders() {
    return ApiResponse.<List<ReturnOrderResponse>>builder()
        .result(returnOrderService.getAllReturnOrders())
        .message(ApiMessage.ALL_RETURN_ORDERS_RETRIEVED.getMessage())
        .build();
  }
}
