package com.market.MSA.services.product;

import com.market.MSA.constants.ABCClassification;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.product.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service to classify products into ABC classes based on Pareto principle (80/15/5 of revenue). */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductClassificationService {

  ProductRepository productRepository;
  OrderDetailRepository orderDetailRepository;

  @Transactional
  public void classifyProducts() {
    // Time window: entire history; could restrict to last year
    LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
    LocalDateTime end = LocalDateTime.now();

    List<Object[]> revenueList = orderDetailRepository.findRevenuePerProduct(start, end);
    double totalRevenue =
        revenueList.stream().mapToDouble(row -> ((Number) row[1]).doubleValue()).sum();

    // Sort by revenue desc
    revenueList.sort(
        (a, b) -> Double.compare(((Number) b[1]).doubleValue(), ((Number) a[1]).doubleValue()));

    double cumulative = 0;
    for (Object[] row : revenueList) {
      Long productId = ((Number) row[0]).longValue();
      double revenue = ((Number) row[1]).doubleValue();
      cumulative += revenue;
      double ratio = cumulative / totalRevenue;
      ABCClassification cls;
      if (ratio <= 0.8) {
        cls = ABCClassification.A;
      } else if (ratio <= 0.95) {
        cls = ABCClassification.B;
      } else {
        cls = ABCClassification.C;
      }
      Product product = productRepository.findById(productId).orElse(null);
      if (product == null) continue;
      product.setAbcClassification(cls);
      product.setTotalRevenue(revenue);
      product.setLastClassificationDate(LocalDateTime.now());
      productRepository.save(product);
    }
    log.info("ABC classification finished for {} products", revenueList.size());
  }
}
