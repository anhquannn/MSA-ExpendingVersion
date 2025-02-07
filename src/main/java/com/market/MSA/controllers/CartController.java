package com.market.MSA.controllers;

import com.market.MSA.requests.CartRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.CartResponse;
import com.market.MSA.services.CartService;
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
    return ApiResponse.<CartResponse>builder().result(cartService.createCart(request)).build();
  }

  @PutMapping("/{cartId}")
  ApiResponse<CartResponse> updateCart(
      @PathVariable Long cartId, @RequestBody @Valid CartRequest request) {
    return ApiResponse.<CartResponse>builder()
        .result(cartService.updateCart(cartId, request))
        .build();
  }

  @DeleteMapping("/{cartId}")
  ApiResponse<String> deleteCart(@PathVariable Long cartId) {
    cartService.deleteCart(cartId);
    return ApiResponse.<String>builder().result("Cart deleted successfully").build();
  }

  @GetMapping("/{cartId}")
  ApiResponse<CartResponse> getCartById(@PathVariable Long cartId) {
    return ApiResponse.<CartResponse>builder().result(cartService.getCartById(cartId)).build();
  }

  @GetMapping("/user/{userId}")
  ApiResponse<CartResponse> getOrCreateCartForUser(@PathVariable Long userId) {
    return ApiResponse.<CartResponse>builder()
        .result(cartService.getOrCreateCartForUser(userId))
        .build();
  }
}
