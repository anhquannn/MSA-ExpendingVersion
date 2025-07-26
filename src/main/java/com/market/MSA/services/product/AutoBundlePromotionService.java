package com.market.MSA.services.product;

import com.market.MSA.constants.ABCClassification;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.Promotion;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.PromotionRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
  InventoryProductRepository inventoryProductRepository;

  /**
   * Generate inactive bundle promotions. Lịch chạy được cấu hình qua Quartz Job {@link
   * com.market.MSA.jobs.AutoBundlePromotionJob}
   */
  @Transactional
  public void generateInactivePromotions() {
    List<Product> mains = productRepository.findByAbcClassification(ABCClassification.A);
    if (mains.isEmpty()) {
      return;
    }

    List<Product> frees = productRepository.findByABCAndNotExempt(ABCClassification.C);
    if (frees.isEmpty()) {
      return;
    }

    // Tính tồn kho dư thừa cho mỗi sản phẩm C
    frees =
        frees.stream()
            .sorted(
                (p1, p2) -> {
                  int stock1 = inventoryProductRepository.totalStockByProductId(p1.getProductId());
                  int stock2 = inventoryProductRepository.totalStockByProductId(p2.getProductId());
                  return Integer.compare(stock2, stock1); // desc
                })
            .toList();

    int created = 0;
    for (Product main : mains) {
      // Chọn sản phẩm C có dư tồn kho nhất (đã sắp xếp ở trên)
      Product free = frees.getFirst();

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
  }
}
