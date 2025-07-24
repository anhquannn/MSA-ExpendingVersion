package com.market.MSA.services.product;

import com.market.MSA.constants.ABCClassification;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.Promotion;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.PromotionRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Tự động sinh khuyến mãi bundle (A -> C) ở trạng thái INACTIVE. Chạy theo lịch (mặc định 1h sáng
 * mỗi ngày).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoBundlePromotionService {

  PromotionRepository promotionRepository;
  ProductRepository productRepository;
  com.market.MSA.repositories.product.InventoryProductRepository inventoryProductRepository;

  /** Generate inactive bundle promotions daily. Adjust cron if needed. */
  @Scheduled(cron = "0 0 1 * * *")
  @Transactional
  public void generateInactivePromotions() {
    List<Product> mains = productRepository.findByAbcClassification(ABCClassification.A);
    if (mains.isEmpty()) {
      log.info("AutoBundlePromotion: no A-class products found.");
      return;
    }

    List<Product> frees =
        productRepository.findByAbcClassificationAndIsExemptFromPromotionFalse(ABCClassification.C);
    if (frees.isEmpty()) {
      log.info("AutoBundlePromotion: no eligible C-class products found.");
      return;
    }

    // Tính tồn kho dư thừa cho mỗi sản phẩm C
    frees =
        frees.stream()
            .sorted(
                (p1, p2) -> {
                  int surplus1 =
                      inventoryProductRepository
                          .findFirstByProduct_ProductId(p1.getProductId())
                          .map(ip -> ip.getStockNumber() - ip.getMinThreshold())
                          .orElse(0);
                  int surplus2 =
                      inventoryProductRepository
                          .findFirstByProduct_ProductId(p2.getProductId())
                          .map(ip -> ip.getStockNumber() - ip.getMinThreshold())
                          .orElse(0);
                  return Integer.compare(surplus2, surplus1); // desc
                })
            .toList();

    int created = 0;
    for (Product main : mains) {
      // Chọn sản phẩm C có dư tồn kho nhất (đã sắp xếp ở trên)
      Product free = frees.get(0);

      boolean dupExists =
          promotionRepository.existsByProductMain_ProductIdAndProductFree_ProductId(
              main.getProductId(), free.getProductId());
      if (dupExists) continue;

      Promotion promo =
          Promotion.builder()
              .productMain(main)
              .productFree(free)
              .startDate(LocalDateTime.now())
              .endDate(LocalDateTime.now().plusMonths(1))
              .discountPercentage(100)
              .status(PromocodeStatus.INACTIVE)
              .build();
      promotionRepository.save(promo);
      created++;
    }
    log.info("AutoBundlePromotion: created {} new inactive promotions.", created);
  }
}
