package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.OrderDetailRequest;
import com.market.MSA.responses.order.OrderDetailResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.OrderDetailService;
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
@RequestMapping("/order-detail")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailController {
  OrderDetailService orderDetailService;

  @PostMapping
  public ApiResponse<OrderDetailResponse> createOrderDetail(
      @RequestBody @Valid OrderDetailRequest request) {
    return ApiResponse.<OrderDetailResponse>builder()
        .result(orderDetailService.createOrderDetail(request))
        .message(ApiMessage.ORDER_DETAIL_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{orderDetailId}")
  public ApiResponse<OrderDetailResponse> updateOrderDetail(
      @PathVariable long orderDetailId, @RequestBody @Valid OrderDetailRequest request) {
    return ApiResponse.<OrderDetailResponse>builder()
        .result(orderDetailService.updateOrderDetail(orderDetailId, request))
        .message(ApiMessage.ORDER_DETAIL_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{orderDetailId}")
  public ApiResponse<Boolean> deleteOrderDetail(@PathVariable long orderDetailId) {
    Boolean result = orderDetailService.deleteOrderDetail(orderDetailId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.ORDER_DETAIL_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{orderDetailId}")
  public ApiResponse<OrderDetailResponse> getOrderDetailById(@PathVariable long orderDetailId) {
    return ApiResponse.<OrderDetailResponse>builder()
        .result(orderDetailService.getOrderDetailById(orderDetailId))
        .message(ApiMessage.ORDER_DETAIL_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/order/{orderId}")
  public ApiResponse<List<OrderDetailResponse>> getOrderDetailsByOrderId(
      @PathVariable long orderId) {
    return ApiResponse.<List<OrderDetailResponse>>builder()
        .result(orderDetailService.getOrderDetailsByOrderId(orderId))
        .message(ApiMessage.ALL_ORDER_DETAILS_RETRIEVED.getMessage())
        .build();
  }
}
