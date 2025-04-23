package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.CancelOrderRequest;
import com.market.MSA.responses.order.CancelOrderResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.CancelOrderService;
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
public class CancelOrderController {
  CancelOrderService cancelOrderService;

  @PostMapping
  public ApiResponse<CancelOrderResponse> createCancelOrder(
      @RequestBody @Valid CancelOrderRequest request) {
    return ApiResponse.<CancelOrderResponse>builder()
        .result(cancelOrderService.createCancelOrder(request))
        .message(ApiMessage.RETURN_ORDER_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<CancelOrderResponse> updateCancelOrder(
      @PathVariable Long id, @RequestBody @Valid CancelOrderRequest request) {
    return ApiResponse.<CancelOrderResponse>builder()
        .result(cancelOrderService.updateCancelOrder(id, request))
        .message(ApiMessage.RETURN_ORDER_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteCancelOrder(@PathVariable Long id) {
    Boolean result = cancelOrderService.deleteCancelOrder(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.RETURN_ORDER_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<CancelOrderResponse> getCancelOrderById(@PathVariable Long id) {
    return ApiResponse.<CancelOrderResponse>builder()
        .result(cancelOrderService.getCancelOrderById(id))
        .message(ApiMessage.RETURN_ORDER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<CancelOrderResponse>> getAllCancelOrders() {
    return ApiResponse.<List<CancelOrderResponse>>builder()
        .result(cancelOrderService.getAllCancelOrders())
        .message(ApiMessage.ALL_RETURN_ORDERS_RETRIEVED.getMessage())
        .build();
  }
}
