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
import org.springframework.cache.annotation.Cacheable;
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

  @Transactional
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
    // 1. Kiểm tra xem chi nhánh có đủ số lượng tồn kho cho sản phẩm hay không.
    boolean hasSufficientStock =
        inventoryProductService.checkStockAvailability(branchId, productId, quantity);
    // Nếu không đủ hàng, ném ra ngoại lệ.
    if (!hasSufficientStock) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
    }

    // 2. Tìm giỏ hàng của người dùng.
    // Nếu người dùng đã có giỏ hàng, sử dụng nó.
    // '.orElseGet()' sẽ được thực thi nếu không tìm thấy giỏ hàng.
    Cart cart =
        cartRepository
            .findByUser_UserId(userId)
            .orElseGet(
                () -> {
                  // Nếu không có, tạo một giỏ hàng mới.
                  Cart newCart = new Cart();
                  // Tìm thông tin người dùng và gán vào giỏ hàng.
                  newCart.setUser(
                      entityFinderService.findByIdOrThrow(
                          userRepository, userId, ErrorCode.USER_NOT_EXISTED));
                  // Đặt trạng thái giỏ hàng là 'ACTIVE'.
                  newCart.setStatus(CartStatus.ACTIVE);
                  // Lưu giỏ hàng mới vào CSDL và trả về.
                  return cartRepository.save(newCart);
                });

    // 3. Kiểm tra xem sản phẩm đã có trong giỏ hàng hay chưa.
    Optional<CartItem> existingCartItem =
        cartItemRepository.findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);

    CartItem cartItem;
    // 4. Xử lý logic dựa trên việc sản phẩm đã tồn tại trong giỏ hay chưa.
    if (existingCartItem.isPresent()) {
      // NẾU SẢN PHẨM ĐÃ CÓ TRONG GIỎ:
      cartItem = existingCartItem.get();
      // Cộng dồn số lượng mới vào số lượng hiện có.
      int newQuantity = cartItem.getQuantity() + quantity;

      // Kiểm tra lại tồn kho với số lượng mới.
      boolean hasEnoughStockForNewQuantity =
          inventoryProductService.checkStockAvailability(branchId, productId, newQuantity);
      if (!hasEnoughStockForNewQuantity) {
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
      }

      // Cập nhật lại số lượng và đánh dấu là đã được chọn.
      cartItem.setQuantity(newQuantity);
      cartItem.setSelected(true);
    } else {
      // NẾU SẢN PHẨM CHƯA CÓ TRONG GIỎ:
      // Tìm thông tin sản phẩm.
      Product product =
          entityFinderService.findByIdOrThrow(
              productRepository, productId, ErrorCode.PRODUCT_NOT_FOUND);

      // Lấy giá bán hiện tại của sản phẩm tại chi nhánh cụ thể.
      double effectivePrice = inventoryProductService.getBranchCurrentPrice(branchId, productId);

      // Tạo một 'CartItem' (mục hàng trong giỏ) mới.
      cartItem =
          CartItem.builder()
              .cart(cart)
              .product(product)
              .quantity(quantity)
              .price(effectivePrice)
              .isSelected(true)
              .build();
    }
    // 5. Lưu 'CartItem' (dù là mới hay được cập nhật) vào CSDL và trả về response cho client.
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

  @Transactional
  public void updateCartItemsSelection(Long cartId, List<Long> cartItemIds, boolean isSelected) {
    if (cartId != null) {
      cartItemRepository.updateCartItemsSelectionByCartId(cartId, isSelected);
    } else {
      cartItemRepository.updateCartItemsSelection(cartItemIds, isSelected);
    }
  }

  @Transactional
  public boolean deleteCartItem(Long cartItemId) {
    if (!cartItemRepository.existsById(cartItemId)) {
      throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
    }
    cartItemRepository.deleteById(cartItemId);
    return true;
  }

  @Transactional
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

  @Cacheable("cart_items_true")
  public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
    List<CartItem> cartItems = cartItemRepository.findByCart_CartIdAndIsSelected(cartId, true);
    return cartItems.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toList());
  }

  @Cacheable("cart_items")
  public List<CartItemResponse> getAllCartItemsByCartId(Long cartId) {
    List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cartId);
    return cartItems.stream().map(cartItemMapper::toCartItemResponse).collect(Collectors.toList());
  }

  public double calculateCartTotal(Long cartId) {
    return cartItemRepository.calculateCartTotal(cartId);
  }
}
