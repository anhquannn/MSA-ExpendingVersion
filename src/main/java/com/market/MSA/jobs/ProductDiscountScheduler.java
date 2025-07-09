package com.market.MSA.jobs;

import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.services.product.ProductService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDiscountScheduler implements Job {

  private final InventoryProductRepository inventoryProductRepository;
  private final ProductService productService;

  /** Runs every day at 2 AM to check for products that need discounts applied */
  @Override
  @Transactional
  public void execute(JobExecutionContext context) {
    // Get current date
    LocalDate today = LocalDate.now();

    // Find all inventory products that are not already discounted
    List<InventoryProduct> productsToDiscount =
        inventoryProductRepository.findByExpDateGreaterThanEqualAndIsDiscountedFalse(today);

    for (InventoryProduct inventoryProduct : productsToDiscount) {
      Product product = inventoryProduct.getProduct();
      LocalDate expDate = inventoryProduct.getExpDate().toLocalDate();

      // Calculate days until expiration
      long daysUntilExpiration = java.time.temporal.ChronoUnit.DAYS.between(today, expDate);

      // If days until expiration is less than or equal to the discount trigger days
      if (daysUntilExpiration <= product.getDiscountTriggerDays()
          && product.getDiscountPercentage() > 0) {
        // Calculate new price with discount
        double originalPrice = product.getPrice();
        double discountAmount = originalPrice * (product.getDiscountPercentage() / 100);
        double newPrice = originalPrice - discountAmount;

        // Update the inventory product
        inventoryProduct.setCurrentPrice(newPrice);
        inventoryProduct.setDiscounted(true);
        inventoryProductRepository.save(inventoryProduct);
      }
    }
  }
}
