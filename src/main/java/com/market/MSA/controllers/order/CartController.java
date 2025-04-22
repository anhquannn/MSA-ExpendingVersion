package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.CartRequest;
import com.market.MSA.responses.order.CartResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.CartService;
import jakarta.validation.Valid;
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
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
  CartService cartService;

  @PostMapping
  ApiResponse<CartResponse> createCart(@RequestBody @Valid CartRequest request) {
    return ApiResponse.<CartResponse>builder()
        .result(cartService.createCart(request))
        .message(ApiMessage.CART_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{cartId}")
  ApiResponse<CartResponse> updateCart(
      @PathVariable Long cartId, @RequestBody @Valid CartRequest request) {
    return ApiResponse.<CartResponse>builder()
        .result(cartService.updateCart(cartId, request))
        .message(ApiMessage.CART_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{cartId}")
  ApiResponse<Boolean> deleteCart(@PathVariable Long cartId) {
    Boolean result = cartService.deleteCart(cartId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.CART_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{cartId}")
  ApiResponse<CartResponse> getCartById(@PathVariable Long cartId) {
    return ApiResponse.<CartResponse>builder()
        .result(cartService.getCartById(cartId))
        .message(ApiMessage.CART_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/user/{userId}")
  ApiResponse<CartResponse> getOrCreateCartForUser(@PathVariable Long userId) {
    return ApiResponse.<CartResponse>builder()
        .result(cartService.getOrCreateCartForUser(userId))
        .message(ApiMessage.CART_RETRIEVED.getMessage())
        .build();
  }
}
