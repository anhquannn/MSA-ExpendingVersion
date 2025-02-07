package com.market.MSA.controllers;

import com.market.MSA.requests.CartItemRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.CartItemResponse;
import com.market.MSA.services.CartItemService;
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
@RequestMapping("/cartitem")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemController {
  CartItemService cartItemService;

  @PostMapping
  ApiResponse<CartItemResponse> createCartItem(@RequestBody @Valid CartItemRequest request) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.createCartItem(request))
        .build();
  }

  @GetMapping("/{cartId}/{productId}")
  ApiResponse<CartItemResponse> getCartItem(
      @PathVariable Long cartId, @PathVariable Long productId) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.getCartItem(cartId, productId))
        .build();
  }

  @PutMapping("/{cartItemId}")
  ApiResponse<CartItemResponse> updateCartItem(
      @PathVariable Long cartItemId, @RequestBody @Valid CartItemRequest request) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.updateCartItem(cartItemId, request))
        .build();
  }

  @PutMapping("/update-status")
  ApiResponse<String> updateCartItemsStatus(
      @RequestBody List<Long> cartItemIds, @RequestParam String status) {
    cartItemService.updateCartItemsStatus(cartItemIds, status);
    return ApiResponse.<String>builder().result("Cart items status updated").build();
  }

  @DeleteMapping("/{cartItemId}")
  ApiResponse<String> deleteCartItem(@PathVariable Long cartItemId) {
    cartItemService.deleteCartItem(cartItemId);
    return ApiResponse.<String>builder().result("Cart item deleted").build();
  }

  @DeleteMapping("/clear/{cartId}")
  ApiResponse<String> clearCart(@PathVariable Long cartId) {
    cartItemService.clearCart(cartId);
    return ApiResponse.<String>builder().result("Cart cleared").build();
  }

  @GetMapping("/by-id/{id}")
  ApiResponse<CartItemResponse> getCartItemById(@PathVariable Long id) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.getCartItemById(id))
        .build();
  }

  @GetMapping("/by-cart/{cartId}")
  ApiResponse<List<CartItemResponse>> getCartItemsByCartId(@PathVariable Long cartId) {
    return ApiResponse.<List<CartItemResponse>>builder()
        .result(cartItemService.getCartItemsByCartId(cartId))
        .build();
  }

  @GetMapping("/all-by-cart/{cartId}")
  ApiResponse<List<CartItemResponse>> getAllCartItemsByCartId(@PathVariable Long cartId) {
    return ApiResponse.<List<CartItemResponse>>builder()
        .result(cartItemService.getAllCartItemsByCartId(cartId))
        .build();
  }

  @PostMapping("/add")
  ApiResponse<CartItemResponse> addProductToCart(
      @RequestParam Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.addProductToCart(cartId, productId, quantity))
        .build();
  }

  @GetMapping("/calculate-total/{cartId}")
  ApiResponse<Double> calculateCartTotal(@PathVariable Long cartId) {
    return ApiResponse.<Double>builder().result(cartItemService.calculateCartTotal(cartId)).build();
  }
}
