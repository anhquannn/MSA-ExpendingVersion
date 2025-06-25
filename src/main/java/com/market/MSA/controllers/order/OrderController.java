package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.requests.filters.OrderFilterRequest;
import com.market.MSA.requests.order.OrderRequest;
import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.order.OrderSummaryResponse;
import com.market.MSA.responses.order.RevenueStatisticsResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
  OrderService orderService;

  @PostMapping
  public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
    OrderResponse response =
        orderService.createOrder(
            request.getUserId(),
            request.getBranchId(),
            request.getCartId(),
            request.getPromoCodes());

    orderService.sendRecipe(response.getOrderId(), response.getUser().getEmail());

    return ApiResponse.<OrderResponse>builder()
        .result(response)
        .message(ApiMessage.ORDER_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{orderId}")
  public ApiResponse<OrderResponse> updateOrder(
      @PathVariable Long orderId, @RequestBody(required = false) @Valid OrderRequest request) {
    return ApiResponse.<OrderResponse>builder()
        .result(orderService.updateOrder(orderId, request))
        .message(ApiMessage.ORDER_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{orderId}")
  public ApiResponse<Boolean> deleteOrder(@PathVariable Long orderId) {
    Boolean result = orderService.deleteOrder(orderId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.ORDER_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{orderId}")
  public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId) {
    return ApiResponse.<OrderResponse>builder()
        .result(orderService.getOrderById(orderId))
        .message(ApiMessage.ORDER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/preview")
  public ApiResponse<OrderSummaryResponse> previewOrder(
      @RequestParam Long userId,
      @RequestParam Long cartId,
      @RequestParam(required = false) List<String> promoCodes) {

    OrderResponse orderSummary = orderService.calculateOrderSummary(userId, cartId, promoCodes);

    OrderSummaryResponse response =
        OrderSummaryResponse.builder()
            .totalCost(orderSummary.getTotalCost())
            .discount(orderSummary.getDiscount())
            .grandTotal(orderSummary.getGrandTotal())
            .build();

    return ApiResponse.<OrderSummaryResponse>builder()
        .result(response)
        .message(ApiMessage.ORDER_SUMMARY_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<OrderResponse>> getAll() {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.getAll())
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<OrderResponse>> getAllOrders(
      @RequestBody @Valid OrderFilterRequest request) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.getAllOrders(request))
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<OrderResponse>> getAllOrdersWithPaging(
      @RequestBody @Valid OrderFilterRequest request) {
    return ApiResponse.<Page<OrderResponse>>builder()
        .result(orderService.getAllOrdersWithPaging(request))
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/revenue/statistics")
  public ApiResponse<RevenueStatisticsResponse> getRevenueStatistics(
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) Long userId) {
    return ApiResponse.<RevenueStatisticsResponse>builder()
        .result(orderService.getRevenueStatistics(year, month, branchId, userId))
        .message(ApiMessage.REVENUE_STATISTICS_RETRIEVED.getMessage())
        .build();
  }

  @PutMapping("/{orderId}/status")
  public ApiResponse<OrderResponse> updateOrderStatus(
      @PathVariable @NotNull(message = "Order ID is required") Long orderId,
      @RequestParam @NotNull(message = "Status is required") String status) {

    if (!OrderStatus.isValidStatus(status)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    OrderResponse response = orderService.updateOrderStatus(orderId, status);
    return ApiResponse.<OrderResponse>builder()
        .result(response)
        .message(ApiMessage.ORDER_UPDATED.getMessage())
        .build();
  }
}
