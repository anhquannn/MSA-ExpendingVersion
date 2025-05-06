package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.OrderRequest;
import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.order.OrderSummaryResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.OrderService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
  public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request)
      throws MessagingException {
    return ApiResponse.<OrderResponse>builder()
        .result(
            orderService.createOrder(
                request.getUserId(),
                request.getBranchId(),
                request.getCartId(),
                request.getPromoCodes()))
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

  @GetMapping("/search")
  public ApiResponse<List<OrderResponse>> searchOrdersByPhoneNumber(
      String phoneNumber, int page, int pageSize) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.searchOrderByPhoneNumber(phoneNumber, page, pageSize))
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/user/{userId}/status/{status}")
  public ApiResponse<List<OrderResponse>> getOrdersByUserIdAndStatus(
      @PathVariable Long userId, @PathVariable String status, int page, int pageSize) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.getOrdersByUserIDWithStatus(userId, status, page, pageSize))
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
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
  public ApiResponse<Page<OrderResponse>> getAllOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "orderDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    return ApiResponse.<Page<OrderResponse>>builder()
        .result(orderService.getAllOrders(page, size, sortBy, sortDirection))
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/branch/{branchId}")
  public ApiResponse<Page<OrderResponse>> getOrdersByBranchId(
      @PathVariable Long branchId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "orderDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    return ApiResponse.<Page<OrderResponse>>builder()
        .result(orderService.getOrdersByBranchId(branchId, page, size, sortBy, sortDirection))
        .message(ApiMessage.ALL_ORDERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/revenue/statistics")
  public ApiResponse<Map<String, Double>> getRevenueStatistics(
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) Long userId) {
    return ApiResponse.<Map<String, Double>>builder()
        .result(orderService.getRevenueStatistics(year, month, branchId, userId))
        .message(ApiMessage.REVENUE_STATISTICS_RETRIEVED.getMessage())
        .build();
  }
}
