package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.CartItemRequest;
import com.market.MSA.requests.order.CartItemSelectionRequest;
import com.market.MSA.responses.order.CartItemResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.CartItemService;
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
@RequestMapping("/cart-item")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemController {
  CartItemService cartItemService;

  @PostMapping
  ApiResponse<CartItemResponse> createCartItem(@RequestBody @Valid CartItemRequest request) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.createCartItem(request))
        .message(ApiMessage.CART_ITEM_CREATED.getMessage())
        .build();
  }

  @GetMapping("/{cartId}/{productId}")
  ApiResponse<CartItemResponse> getCartItem(
      @PathVariable Long cartId, @PathVariable Long productId) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.getCartItem(cartId, productId))
        .message(ApiMessage.CART_ITEM_RETRIEVED.getMessage())
        .build();
  }

  @PutMapping("/{cartItemId}")
  public ApiResponse<CartItemResponse> updateCartItem(
      @PathVariable Long cartItemId,
      @RequestParam Long branchId,
      @RequestParam int quantity,
      @RequestParam(defaultValue = "true") boolean isSelected) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.updateCartItem(cartItemId, branchId, quantity, isSelected))
        .message(ApiMessage.CART_ITEM_UPDATED.getMessage())
        .build();
  }

  @PutMapping("/update-selection")
  ApiResponse<String> updateCartItemsSelection(
      @RequestBody CartItemSelectionRequest request, @RequestParam boolean isSelected) {
    cartItemService.updateCartItemsSelection(request.getCartItemIds(), isSelected);
    return ApiResponse.<String>builder()
        .result("Cart items selection updated")
        .message(ApiMessage.CART_ITEMS_SELECTION_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{cartItemId}")
  ApiResponse<Boolean> deleteCartItem(@PathVariable Long cartItemId) {
    Boolean result = cartItemService.deleteCartItem(cartItemId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.CART_ITEM_DELETED.getMessage())
        .build();
  }

  @DeleteMapping("/clear/{cartId}")
  ApiResponse<String> clearCart(@PathVariable Long cartId) {
    cartItemService.clearCart(cartId);
    return ApiResponse.<String>builder()
        .result("Cart cleared")
        .message(ApiMessage.CART_CLEARED.getMessage())
        .build();
  }

  @GetMapping("/by-id/{id}")
  ApiResponse<CartItemResponse> getCartItemById(@PathVariable Long id) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.getCartItemById(id))
        .message(ApiMessage.CART_ITEM_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/by-cart/{cartId}")
  ApiResponse<List<CartItemResponse>> getCartItemsByCartId(@PathVariable Long cartId) {
    return ApiResponse.<List<CartItemResponse>>builder()
        .result(cartItemService.getCartItemsByCartId(cartId))
        .message(ApiMessage.CART_ITEM_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/all-by-cart/{cartId}")
  ApiResponse<List<CartItemResponse>> getAllCartItemsByCartId(@PathVariable Long cartId) {
    return ApiResponse.<List<CartItemResponse>>builder()
        .result(cartItemService.getAllCartItemsByCartId(cartId))
        .message(ApiMessage.ALL_CART_ITEMS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/add")
  public ApiResponse<CartItemResponse> addToCart(
      @RequestParam Long userId,
      @RequestParam Long productId,
      @RequestParam Long branchId,
      @RequestParam int quantity) {
    return ApiResponse.<CartItemResponse>builder()
        .result(cartItemService.addToCart(userId, productId, branchId, quantity))
        .message(ApiMessage.ADD_CART_ITEM.getMessage())
        .build();
  }

  @GetMapping("/calculate-total/{cartId}")
  ApiResponse<Double> calculateCartTotal(@PathVariable Long cartId) {
    return ApiResponse.<Double>builder()
        .result(cartItemService.calculateCartTotal(cartId))
        .message(ApiMessage.CART_TOTAL_CALCULATED.getMessage())
        .build();
  }
}
