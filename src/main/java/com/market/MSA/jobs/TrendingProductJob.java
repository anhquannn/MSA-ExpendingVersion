package com.market.MSA.jobs;

import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.TrendingProduct;
import com.market.MSA.repositories.product.FeedbackRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.TrendingProductRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TrendingProductJob implements Job {
  private final FeedbackRepository feedbackRepository;
  private final ProductRepository productRepository;
  private final TrendingProductRepository trendingProductRepository;

  // Constructor injection
  public TrendingProductJob(
      FeedbackRepository feedbackRepository,
      ProductRepository productRepository,
      TrendingProductRepository trendingProductRepository) {
    this.feedbackRepository = feedbackRepository;
    this.productRepository = productRepository;
    this.trendingProductRepository = trendingProductRepository;
  }

  @Override
  @Transactional
  public void execute(JobExecutionContext context) {
    List<Object[]> avgRatingList = feedbackRepository.findAverageRatingsByProduct();
    Map<Long, Double> avgRatings = new HashMap<>();
    for (Object[] avgRating : avgRatingList) {
      Long productId = (Long) avgRating[0];
      Double averageRating = (Double) avgRating[1];
      avgRatings.put(productId, averageRating);
    }

    List<Product> products = productRepository.findAll();
    List<TrendingProduct> candidates =
        products.stream()
            .map(
                product -> {
                  Long productId = product.getProductId();
                  Date currentDate = new Date();
                  double avgRating = avgRatings.getOrDefault(productId, 0.0);
                  double totalRevenue = product.getTotalRevenue();
                  double trendScore = calculateTrendScore(avgRating, totalRevenue);

                  return new TrendingProduct(0L, trendScore, currentDate, product);
                })
            .sorted((c1, c2) -> Double.compare(c2.getTrendScore(), c1.getTrendScore()))
            .limit(5)
            .toList();

    trendingProductRepository.deleteAllTrendingProducts();

    List<TrendingProduct> trendingProducts =
        candidates.stream()
            .map(
                candidate ->
                    TrendingProduct.builder()
                        .product(candidate.getProduct())
                        .trendScore(candidate.getTrendScore())
                        .timestamp(new Date())
                        .build())
            .toList();

    trendingProductRepository.saveAll(trendingProducts);
  }

  private double calculateTrendScore(double avgRating, double totalRevenue) {
    double weightRating = 0.3;
    double weightSales = 0.7;
    return (avgRating * weightRating) + (totalRevenue * weightSales);
  }
}
