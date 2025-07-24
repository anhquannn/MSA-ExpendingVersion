package com.market.MSA.services.product;

import com.market.MSA.constants.ABCClassification;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.PromotionMapper;
import com.market.MSA.models.order.CartItem;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.Promotion;
import com.market.MSA.repositories.order.CartItemRepository;
import com.market.MSA.repositories.order.CartRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.PromotionRepository;
import com.market.MSA.requests.filters.PromotionFilterRequest;
import com.market.MSA.requests.product.PromotionRequest;
import com.market.MSA.responses.product.PromotionResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.others.NotificationService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
  InventoryProductRepository inventoryProductRepository;
  NotificationService notificationService;
  private final EntityFinderService entityFinderService;
  private final ProductRepository productRepository;

  /* ================= CRUD ================= */
  @Transactional
  public PromotionResponse createPromotion(PromotionRequest request) {
    Promotion promotion = promotionMapper.toPromotion(request);
    promotion.setProductMain(entityFinderService.findByIdOrThrow(productRepository, request.getProductMainId(), ErrorCode.PRODUCT_NOT_FOUND));
    promotion.setProductFree(entityFinderService.findByIdOrThrow(productRepository, request.getProductFreeId(), ErrorCode.PRODUCT_NOT_FOUND));

    promotion.setStatus(Optional.ofNullable(request.getStatus()).orElse(PromocodeStatus.INACTIVE));
    promotion = promotionRepository.save(promotion);
    return promotionMapper.toPromotionResponse(promotion);
  }

  @Transactional
  public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
    promotion.setProductMain(entityFinderService.findByIdOrThrow(productRepository, request.getProductMainId(), ErrorCode.PRODUCT_NOT_FOUND));
    promotion.setProductFree(entityFinderService.findByIdOrThrow(productRepository, request.getProductFreeId(), ErrorCode.PRODUCT_NOT_FOUND));

    promotionMapper.updatePromotionFromRequest(request, promotion);
    promotion = promotionRepository.save(promotion);
    return promotionMapper.toPromotionResponse(promotion);
  }

  @Transactional
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
  public List<PromotionResponse> getAll() {
    return promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate")).stream()
        .map(promotionMapper::toPromotionResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("promotions_list")
  public List<PromotionResponse> getAllPromotions(PromotionFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return promotionRepository.filter(request.getKeyword(), request.getStatus(), sort).stream()
        .map(promotionMapper::toPromotionResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("promotions_paging")
  public Page<PromotionResponse> getAllPromotionsWithPaging(PromotionFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);
    return promotionRepository
        .filterWithPaging(request.getKeyword(), request.getStatus(), pageable)
        .map(promotionMapper::toPromotionResponse);
  }

  /* ================= Bundle Promotion Logic ================= */
  @Transactional
  public void applyBundlePromotions(Long cartId) {
    var cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    List<CartItem> items = cartItemRepository.findByCart_CartId(cartId);

    for (CartItem item : items) {
      Long mainProductId = item.getProduct().getProductId();
      List<Promotion> promotions =
          promotionRepository.findActiveByProductMain(mainProductId, LocalDateTime.now());
      for (Promotion promo : promotions) {
        Product freeProduct = promo.getProductFree();
        // Check existing free item in cart
        boolean alreadyAddedFree =
            cartItemRepository
                .findByCart_CartIdAndProduct_ProductIdAndIsFreeItemTrue(
                    cartId, freeProduct.getProductId())
                .isPresent();
        if (alreadyAddedFree) continue;

        // Kiểm tra tồn kho & ABC & exempt
        Optional<InventoryProduct> invOpt =
            inventoryProductRepository.findFirstByProduct_ProductId(freeProduct.getProductId());
        if (invOpt.isEmpty()) continue;
        InventoryProduct inv = invOpt.get();
        boolean eligible =
            inv.getStockNumber() > inv.getMinThreshold()
                && freeProduct.getAbcClassification() == ABCClassification.C
                && !freeProduct.isExemptFromPromotion();
        if (!eligible) continue;

        // Add free item
        CartItem freeCartItem =
            CartItem.builder()
                .cart(cart)
                .product(freeProduct)
                .quantity(item.getQuantity())
                .price(0.0)
                .isFreeItem(true)
                .build();
        cartItemRepository.save(freeCartItem);

        notificationService.notifyUser(
            cart.getUser().getUserId(),
            "Bạn được tặng "
                + freeProduct.getName()
                + " miễn phí khi mua "
                + item.getProduct().getName());
      }
    }
  }
}
