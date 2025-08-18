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
import com.market.MSA.repositories.product.PromotionRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.order.CartItemRequest;
import com.market.MSA.responses.order.CartItemResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.product.InventoryProductService;
import com.market.MSA.services.product.PromotionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  final PromotionService promotionService;
  final PromotionRepository promotionRepository;

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
    CartItem saved = cartItemRepository.save(cartItem);
    // Sau khi thêm/cập nhật CartItem, hệ thống sẽ rà soát xem giỏ hàng có đủ
    // điều kiện khuyến mãi gói (bundle promotion) hay không.
    // Nếu thỏa, PromotionService sẽ tự động tạo/ cập nhật các mục giảm giá tương ứng
    // (ví dụ: mua 2 tặng 1, mua combo giảm 10%, ...).
    // Lưu ý: hàm này KHÔNG giảm giá trực tiếp ở đây, nó chỉ chuẩn bị dữ liệu
    // để tính toán ở bước "preview" và "checkout".
    // Kích hoạt khuyến mãi bundle tự động
    // Truyền cả cartId và branchId để kiểm tra tồn kho chính xác
    promotionService.applyBundlePromotions(cart.getCartId());
    return cartItemMapper.toCartItemResponse(saved);
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

    // After update, ensure bundle items remain consistent with new selection/quantity
    synchronizeBundleItems(cartItem.getCart().getCartId());
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
    // Synchronize bundle items after bulk selection change
    synchronizeBundleItems(cartId);
  }

  @Transactional
  public boolean deleteCartItem(Long cartItemId) {
    // Retrieve the cart item first to know its type (main or free) and cart reference
    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

    Long cartId = cartItem.getCart().getCartId();
    boolean isMainItem = !cartItem.isFreeItem();

    // Delete the requested cart item
    cartItemRepository.deleteById(cartItemId);

    // If the deleted item is a main product, refresh bundle (free) items
    if (isMainItem) {
      // This will remove all current free items and re-apply promotions based on
      // the remaining main items in the cart, effectively cascading the deletion
      // of free products associated with the removed main product.
      synchronizeBundleItems(cartId);
    }
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

  @Transactional
  @Cacheable("cart_items_true")
  public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
    // Ensure free items are synchronized based on current selections before returning list
    synchronizeBundleItems(cartId);
    List<CartItem> cartItems =
        cartItemRepository.findWithDetailsByCartIdAndIsSelected(cartId, true);
    return buildHierarchicalResponses(cartItems);
  }

  @Transactional
  @Cacheable("cart_items")
  public List<CartItemResponse> getAllCartItemsByCartId(Long cartId) {
    // Synchronize free items to reflect current state (selected and non-selected)
    synchronizeBundleItems(cartId);
    List<CartItem> cartItems = cartItemRepository.findWithDetailsByCartId(cartId);
    return buildHierarchicalResponses(cartItems);
  }

  /**
   * Refresh bundle (free) items to ensure consistency before any pricing operation. Logic: remove
   * all free items, then re-apply bundle promotions based on current selected main items.
   */
  private List<CartItemResponse> buildHierarchicalResponses(List<CartItem> cartItems) {
    Map<Long, CartItemResponse> mainMap = new LinkedHashMap<>();
    List<CartItemResponse> result = new ArrayList<>();

    // build map for main items
    for (CartItem item : cartItems) {
      if (!item.isFreeItem()) {
        CartItemResponse resp = cartItemMapper.toCartItemResponse(item);
        resp.setFreeItems(new ArrayList<>());
        mainMap.put(item.getProduct().getProductId(), resp);
        result.add(resp);
      }
    }

    // Pre-fetch active promotions for all main products to avoid N+1 queries
    List<Long> mainIds = new ArrayList<>(mainMap.keySet());
    Map<Long, List<Long>> mainToFreeMap = new HashMap<>();
    if (!mainIds.isEmpty()) {
      promotionRepository
          .findActiveByProductMainIn(mainIds, java.time.LocalDateTime.now())
          .forEach(
              p ->
                  mainToFreeMap
                      .computeIfAbsent(p.getProductMain().getProductId(), k -> new ArrayList<>())
                      .add(p.getProductFree().getProductId()));
    }

    // Attach free items to their corresponding main items
    for (CartItem freeItem : cartItems) {
      if (!freeItem.isFreeItem()) continue;
      Long freeProductId = freeItem.getProduct().getProductId();
      Long matchedMain = null;
      for (var entry : mainToFreeMap.entrySet()) {
        if (entry.getValue().contains(freeProductId)) {
          matchedMain = entry.getKey();
          break;
        }
      }
      if (matchedMain != null) {
        mainMap.get(matchedMain).getFreeItems().add(cartItemMapper.toCartItemResponse(freeItem));
      } else {
        result.add(cartItemMapper.toCartItemResponse(freeItem));
      }
    }
    return result;
  }

  @Transactional
  public void synchronizeBundleItems(Long cartId) {
    // Remove current free items
    cartItemRepository.deleteFreeItemsByCartId(cartId);
    // Re-apply promotions to add correct free items
    promotionService.applyBundlePromotions(cartId);
  }

  @Transactional
  public double calculateCartTotal(Long cartId) {
    // Ensure free items reflect current cart state
    synchronizeBundleItems(cartId);
    return cartItemRepository.calculateCartTotal(cartId);
  }
}
