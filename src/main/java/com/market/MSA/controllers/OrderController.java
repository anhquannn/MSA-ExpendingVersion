package com.market.MSA.controllers;

import com.market.MSA.requests.OrderRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.OrderResponse;
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
                request.getUserId(), request.getCartId(), request.getPromoCode()))
        .build();
  }

  @PutMapping("/{orderId}")
  public ApiResponse<OrderResponse> updateOrder(
      @PathVariable Long orderId, @RequestBody @Valid OrderRequest request) {
    return ApiResponse.<OrderResponse>builder()
        .result(orderService.updateOrder(orderId, request))
        .build();
  }

  @DeleteMapping("/{orderId}")
  public ApiResponse<String> deleteOrder(@PathVariable Long orderId) {
    orderService.deleteOrder(orderId);
    return ApiResponse.<String>builder().result("Order has been deleted successfully.").build();
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
  public ApiResponse<OrderResponse> previewOrder(
      @RequestParam Long userId, @RequestParam Long cartId, @RequestParam String promoCode) {
    return ApiResponse.<OrderResponse>builder()
        .result(orderService.calculateOrderSummary(userId, cartId, promoCode))
        .build();
  }

  @GetMapping
  public ApiResponse<List<OrderResponse>> getAllOrders(int page, int pageSize) {
    return ApiResponse.<List<OrderResponse>>builder()
        .result(orderService.getAllOrders(page, pageSize))
        .build();
  }
}
