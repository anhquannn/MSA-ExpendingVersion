package com.market.MSA.services.order;

import com.market.MSA.constants.CartStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.CartItemMapper;
import com.market.MSA.models.order.Cart;
import com.market.MSA.models.order.CartItem;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.order.CartItemRepository;
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.order.CartItemRequest;
import com.market.MSA.responses.order.CartItemResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.product.InventoryProductService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {
  final EntityFinderService entityFinderService;

  final CartItemRepository cartItemRepository;
  final CartRepository cartRepository;
  final ProductRepository productRepository;
  final UserRepository userRepository;

  final CartItemMapper cartItemMapper;

  final InventoryProductService inventoryProductService;

  public CartItemResponse createCartItem(CartItemRequest request) {
    CartItem cartItem = cartItemMapper.toCartItem(request);
    cartItem.setCart(
        entityFinderService.findByIdOrThrow(
            cartRepository, request.getCartId(), ErrorCode.CART_NOT_FOUND));
    cartItem.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

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

  @Transactional
  public CartItemResponse addToCart(Long userId, Long productId, Long branchId, int quantity) {
    boolean hasSufficientStock =
        inventoryProductService.checkStockAvailability(branchId, productId, quantity);
    if (!hasSufficientStock) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
    }

    Cart cart =
        cartRepository
            .findByUser_UserId(userId)
            .orElseGet(
                () -> {
                  Cart newCart = new Cart();
                  newCart.setUser(
                      entityFinderService.findByIdOrThrow(
                          userRepository, userId, ErrorCode.USER_NOT_EXISTED));
                  newCart.setStatus(CartStatus.CART_STATUS_1.getStatus());
                  return cartRepository.save(newCart);
                });

    Optional<CartItem> existingCartItem =
        cartItemRepository.findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);

    CartItem cartItem;
    if (existingCartItem.isPresent()) {
      cartItem = existingCartItem.get();
      int newQuantity = cartItem.getQuantity() + quantity;

      boolean hasEnoughStockForNewQuantity =
          inventoryProductService.checkStockAvailability(branchId, productId, newQuantity);
      if (!hasEnoughStockForNewQuantity) {
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
      }

      cartItem.setQuantity(newQuantity);
    } else {
      Product product =
          entityFinderService.findByIdOrThrow(
              productRepository, productId, ErrorCode.PRODUCT_NOT_FOUND);

      cartItem =
          CartItem.builder()
              .cart(cart)
              .product(product)
              .quantity(quantity)
              .price(product.getCurrentPrice())
              .isSelected(true)
              .build();
    }
    return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem));
  }

  @Transactional
  public CartItemResponse updateCartItem(
      Long cartItemId, Long branchId, int quantity, boolean isSelected) {
    // Check if cart item exists
    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

    // Check stock availability
    boolean hasSufficientStock =
        inventoryProductService.checkStockAvailability(
            branchId, cartItem.getProduct().getProductId(), quantity);
    if (!hasSufficientStock) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
    }

    // Update cart item using repository method
    cartItemRepository.updateCartItem(cartItemId, isSelected, quantity);

    // Get updated cart item
    return getCartItemById(cartItemId);
  }

  public void updateCartItemsSelection(List<Long> cartItemIds, boolean isSelected) {
    cartItemRepository.updateCartItemsSelection(cartItemIds, isSelected);
  }

  public boolean deleteCartItem(Long cartItemId) {
    if (!cartItemRepository.existsById(cartItemId)) {
      throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
    }
    cartItemRepository.deleteById(cartItemId);
    return true;
  }

  public void clearCart(Long cartId) {
    cartItemRepository.clearCart(cartId);
  }

  public CartItemResponse getCartItemById(Long id) {
    CartItem cartItem =
        cartItemRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    return cartItemMapper.toCartItemResponse(cartItem);
  }

  public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
    List<CartItem> cartItems = cartItemRepository.findByCart_CartIdAndIsSelected(cartId, true);
    return cartItems.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toList());
  }

  public List<CartItemResponse> getAllCartItemsByCartId(Long cartId) {
    List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cartId);
    return cartItems.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toList());
  }

  public double calculateCartTotal(Long cartId) {
    return cartItemRepository.calculateCartTotal(cartId);
  }
}
