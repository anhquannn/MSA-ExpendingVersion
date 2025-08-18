package com.market.MSA.services.product;

import com.market.MSA.constants.ABCClassification;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.PromotionMapper;
import com.market.MSA.models.order.Cart;
import com.market.MSA.models.order.CartItem;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.Promotion;
import com.market.MSA.repositories.order.CartItemRepository;
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.PromotionRepository;
import com.market.MSA.requests.filters.PromotionFilterRequest;
import com.market.MSA.requests.product.PromotionRequest;
import com.market.MSA.responses.product.PromotionResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.others.NotificationService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service quản lý Promotion: CRUD + APIs lấy danh sách + logic tặng sản phẩm C. */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionService {

  PromotionRepository promotionRepository;
  PromotionMapper promotionMapper;
  // ---- Dependencies cho bundle promo ----
  CartRepository cartRepository;
  CartItemRepository cartItemRepository;
  NotificationService notificationService;
  final EntityFinderService entityFinderService;
  final ProductRepository productRepository;

  /* ================= CRUD ================= */
  @Transactional
  @CacheEvict(
      value = {"all_promotions", "promotions_list", "promotions_paging"},
      allEntries = true)
  public PromotionResponse createPromotion(PromotionRequest request) {
    // Kiểm tra trùng lặp (productMain + productFree)
    boolean exists =
        promotionRepository.existsByProductMain_ProductIdAndProductFree_ProductId(
            request.getProductMainId(), request.getProductFreeId());
    if (exists) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    // Validate discount trigger days for both products > 30
    Product mainProduct =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductMainId(), ErrorCode.PRODUCT_NOT_FOUND);
    Product freeProduct =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductFreeId(), ErrorCode.PRODUCT_NOT_FOUND);
    if (mainProduct.getDiscountTriggerDays() <= 30 || freeProduct.getDiscountTriggerDays() <= 30) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    Promotion promotion = promotionMapper.toPromotion(request);
    // Gán lại product sau khi đã validate
    promotion.setProductMain(mainProduct);
    promotion.setProductFree(freeProduct);

    promotion.setStatus(Optional.ofNullable(request.getStatus()).orElse(PromocodeStatus.INACTIVE));
    promotion = promotionRepository.save(promotion);
    return promotionMapper.toPromotionResponse(promotion);
  }

  @Transactional
  @CacheEvict(
      value = {"all_promotions", "promotions_list", "promotions_paging"},
      allEntries = true)
  public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
    promotion.setProductMain(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductMainId(), ErrorCode.PRODUCT_NOT_FOUND));
    promotion.setProductFree(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductFreeId(), ErrorCode.PRODUCT_NOT_FOUND));

    // Validate discount trigger days for both products > 30
    if (promotion.getProductMain().getDiscountTriggerDays() <= 30
        || promotion.getProductFree().getDiscountTriggerDays() <= 30) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    // Kiểm tra trùng lặp (productMain + productFree) khi cập nhật
    boolean duplicateExists =
        promotionRepository.existsByProductMain_ProductIdAndProductFree_ProductId(
                request.getProductMainId(), request.getProductFreeId())
            && !(promotion.getProductMain().getProductId().equals(request.getProductMainId())
                && promotion.getProductFree().getProductId().equals(request.getProductFreeId()));
    if (duplicateExists) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    promotionMapper.updatePromotionFromRequest(request, promotion);
    promotion = promotionRepository.save(promotion);
    return promotionMapper.toPromotionResponse(promotion);
  }

  @Transactional
  @CacheEvict(
      value = {"all_promotions", "promotions_list", "promotions_paging"},
      allEntries = true)
  public boolean deletePromotion(Long id) {
    if (!promotionRepository.existsById(id)) {
      throw new AppException(ErrorCode.PROMOTION_NOT_FOUND);
    }
    promotionRepository.deleteById(id);
    return true;
  }

  public PromotionResponse getPromotionById(Long id) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
    return promotionMapper.toPromotionResponse(promotion);
  }

  /* ================= LIST ================= */
  @Cacheable("all_promotions")
  @Transactional(readOnly = true)
  public List<PromotionResponse> getAll() {
    return promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate")).stream()
        .map(promotionMapper::toPromotionResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("promotions_list")
  @Transactional(readOnly = true)
  public List<PromotionResponse> getAllPromotions(PromotionFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return promotionRepository.filter(request.getKeyword(), request.getStatus(), sort).stream()
        .map(promotionMapper::toPromotionResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("promotions_paging")
  @Transactional(readOnly = true)
  public Page<PromotionResponse> getAllPromotionsWithPaging(PromotionFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);
    return promotionRepository
        .filterWithPaging(request.getKeyword(), request.getStatus(), pageable)
        .map(promotionMapper::toPromotionResponse);
  }

  /* ================= Bundle Promotion Logic ================= */
  /**
   * Áp dụng khuyến mãi dạng mua hàng tặng quà cho giỏ hàng
   *
   * @param cartId ID của giỏ hàng cần áp dụng khuyến mãi
   */
  @Transactional
  public void applyBundlePromotions(Long cartId) {
    // Kiểm tra tham số đầu vào
    if (cartId == null) {
      return;
    }

    // Lấy tất cả sản phẩm trong giỏ hàng
    List<CartItem> items = cartItemRepository.findByCart_CartId(cartId);
    if (items.isEmpty()) {
      return;
    }

    // Lấy thông tin giỏ hàng
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    // Phân loại sản phẩm thành 2 nhóm: sản phẩm thường và sản phẩm free
    Map<Long, List<CartItem>> mainProductToFreeItems = new HashMap<>();
    List<CartItem> freeItems = new ArrayList<>();

    for (CartItem item : items) {
      if (item.isFreeItem()) {
        freeItems.add(item);
      } else {
        mainProductToFreeItems.put(item.getProduct().getProductId(), new ArrayList<>());
      }
    }

    // Duyệt qua từng sản phẩm trong giỏ hàng để áp dụng khuyến mãi
    for (CartItem item : items) {
      // Bỏ qua nếu là sản phẩm free
      if (item.isFreeItem()) continue;

      // Lấy thông tin sản phẩm chính
      Long mainProductId = item.getProduct().getProductId();

      // Tìm tất cả khuyến mãi đang hoạt động cho sản phẩm này
      List<Promotion> promotions =
          promotionRepository.findActiveByProductMain(mainProductId, LocalDateTime.now());
      if (promotions.isEmpty()) continue;

      // Duyệt qua từng khuyến mãi
      for (Promotion promo : promotions) {
        Product freeProduct = promo.getProductFree();

        // Kiểm tra xem sản phẩm free này đã có trong giỏ chưa
        Optional<CartItem> existingFreeItem =
            freeItems.stream()
                .filter(f -> f.getProduct().getProductId().equals(freeProduct.getProductId()))
                .findFirst();

        // Nếu sản phẩm free đã tồn tại trong giỏ
        if (existingFreeItem.isPresent()) {
          CartItem freeItem = existingFreeItem.get();
          boolean needsUpdate = false;

          // Nếu main bỏ chọn nhưng free vẫn được chọn -> biến free thành sản phẩm thường và cập
          // nhật giá thực
          if (!item.isSelected() && freeItem.isSelected()) {
            freeItem.setFreeItem(false);
            double realPrice = freeProduct.getPrice();
            freeItem.setPrice(realPrice);
            needsUpdate = true;
          }

          // Nếu main được chọn lại và freeItem trước đó đã trở thành sản phẩm thường -> đưa về 0đ
          if (item.isSelected() && !freeItem.isFreeItem()) {
            freeItem.setFreeItem(true);
            freeItem.setPrice(0.0);
            // Đồng bộ trạng thái chọn với main (free phải được tặng kèm)
            freeItem.setSelected(item.isSelected());
            // Số lượng cũng đồng bộ phía dưới nhưng đảm bảo giá đã về 0
            needsUpdate = true;
          }

          // Đồng bộ trạng thái chọn với sản phẩm chính
          if (freeItem.isSelected() != item.isSelected()) {
            freeItem.setSelected(item.isSelected());
            needsUpdate = true;
          }
          // Đồng bộ số lượng với sản phẩm chính
          if (freeItem.getQuantity() != item.getQuantity()) {
            freeItem.setQuantity(item.getQuantity());
            needsUpdate = true;
          }

          // Lưu thay đổi nếu cần
          if (needsUpdate) {
            cartItemRepository.save(freeItem);
          }
          continue;
        }

        // Kiểm tra điều kiện để thêm sản phẩm free mới
        // 1. Kiểm tra điều kiện sản phẩm free:
        // - Phân loại ABC phải là C
        // - Không nằm trong danh sách miễn khuyến mãi
        if (freeProduct.getAbcClassification() != ABCClassification.C
            || freeProduct.isExemptFromPromotion()) {
          continue;
        }

        try {
          // Tạo mới sản phẩm free
          CartItem freeCartItem =
              CartItem.builder()
                  .cart(cart)
                  .product(freeProduct)
                  .quantity(item.getQuantity())
                  .price(0.0) // Giá 0 đồng vì là quà tặng
                  .isSelected(item.isSelected()) // Đồng bộ trạng thái chọn với sản phẩm chính
                  .isFreeItem(true)
                  .build();

          // Lưu vào CSDL
          cartItemRepository.save(freeCartItem);
          freeItems.add(freeCartItem);

          // Gửi thông báo cho người dùng
          notificationService.notifyUser(
              cart.getUser().getUserId(),
              String.format(
                  "Bạn được tặng %s miễn phí khi mua %s",
                  freeProduct.getName(), item.getProduct().getName()));
        } catch (Exception e) {
          throw new AppException(ErrorCode.UNAUTHORIZED);
        }
      }
    }
  }
}
