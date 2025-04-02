package com.market.MSA.controllers;

import com.market.MSA.requests.OrderRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.OrderResponse;
import com.market.MSA.responses.OrderSummaryResponse;
import com.market.MSA.services.OrderService;
import jakarta.mail.MessagingException;
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
    // Truyền danh sách promoCodeIds vào service
    return ApiResponse.<OrderResponse>builder()
        .result(
            orderService.createOrder(
                request.getUserId(),
                request.getCartId(),
                request.getBranchId(),
                request.getPromoCodes()))
        .build();
  }

  @PutMapping("/{orderId}")
  public ApiResponse<OrderResponse> updateOrder(
      @PathVariable Long orderId, @RequestBody(required = false) @Valid OrderRequest request) {
    return ApiResponse.<OrderResponse>builder()
        .result(orderService.updateOrder(orderId, request))
        .build();
  }

  @DeleteMapping("/{orderId}")
  public ApiResponse<Boolean> deleteOrder(@PathVariable Long orderId) {
    Boolean result = orderService.deleteOrder(orderId);
    return ApiResponse.<Boolean>builder().result(result).build();
  }

  @GetMapping("/{orderId}")
  public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId) {
    return ApiResponse.<OrderResponse>builder().result(orderService.getOrderById(orderId)).build();
  }

  @GetMapping("/search")
  public ApiResponse<List<OrderResponse>> searchOrdersByPhoneNumber(
      String phoneNumber, int page, int pageSize) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.searchOrderByPhoneNumber(phoneNumber, page, pageSize))
        .build();
  }

  @GetMapping("/user/{userId}/status/{status}")
  public ApiResponse<List<OrderResponse>> getOrdersByUserIdAndStatus(
      @PathVariable Long userId, @PathVariable String status, int page, int pageSize) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.getOrdersByUserIDWithStatus(userId, status, page, pageSize))
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

    return ApiResponse.<OrderSummaryResponse>builder().result(response).build();
  }

  @GetMapping
  public ApiResponse<List<OrderResponse>> getAllOrders(int page, int pageSize) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.getAllOrders(page, pageSize))
        .build();
  }
}
