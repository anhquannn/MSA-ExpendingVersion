package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.CartItemMapper;
import com.market.MSA.models.Cart;
import com.market.MSA.models.CartItem;
import com.market.MSA.models.Product;
import com.market.MSA.repositories.CartItemRepository;
import com.market.MSA.repositories.CartRepository;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.requests.CartItemRequest;
import com.market.MSA.responses.CartItemResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {
  CartItemRepository cartItemRepository;
  CartRepository cartRepository;
  ProductRepository productRepository;
  CartItemMapper cartItemMapper;

  public CartItemResponse createCartItem(CartItemRequest request) {
    Cart cart =
        cartRepository
            .findById(request.getCartId())
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    Product product =
        productRepository
            .findById(request.getProductId())
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    CartItem cartItem = cartItemMapper.toCartItem(request);
    cartItem.setCart(cart);
    cartItem.setProduct(product);

    cartItem = cartItemRepository.save(cartItem);
    return cartItemMapper.toCartItemResponse(cartItem);
  }

  public CartItemResponse getCartItem(Long cartId, Long productId) {
    Optional<CartItem> cartItem =
        cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId);
    return cartItem
        .map(cartItemMapper::toCartItemResponse)
        .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
  }

  public CartItemResponse updateCartItem(Long cartItemId, CartItemRequest request) {
    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    cartItemMapper.updateCartItemFromRequest(request, cartItem);
    cartItemRepository.save(cartItem);
    return cartItemMapper.toCartItemResponse(cartItem);
  }

  public void updateCartItemsStatus(List<Long> cartItemIds, String status) {
    cartItemRepository.updateCartItemsStatus(cartItemIds, status);
  }

  public void deleteCartItem(Long cartItemId) {
    cartItemRepository.deleteById(cartItemId);
  }

  public void clearCart(Long cartId) {
    cartItemRepository.clearCart(cartId, "available");
  }

  public CartItemResponse getCartItemById(Long id) {
    CartItem cartItem =
        cartItemRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    return cartItemMapper.toCartItemResponse(cartItem);
  }

  public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
    List<CartItem> cartItems = cartItemRepository.findByCart_CartIdAndStatus(cartId, "available");
    return cartItems.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toList());
  }

  public List<CartItemResponse> getAllCartItemsByCartId(Long cartId) {
    List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cartId);
    return cartItems.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toList());
  }

  public CartItemResponse addProductToCart(Long cartId, Long productId, int quantity) {
    Optional<CartItem> existingCartItem =
        cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId);
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    if (existingCartItem.isPresent()) {
      CartItem cartItem = existingCartItem.get();
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
      cartItemRepository.save(cartItem);
      return cartItemMapper.toCartItemResponse(cartItem);
    }

    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    CartItem newCartItem =
        CartItem.builder()
            .cart(cart)
            .product(product)
            .quantity(quantity)
            .status("unavailable")
            .price(product.getPrice())
            .build();

    cartItemRepository.save(newCartItem);
    return cartItemMapper.toCartItemResponse(newCartItem);
  }

  public double calculateCartTotal(Long cartId) {
    return cartItemRepository.calculateCartTotal(cartId);
  }
}
