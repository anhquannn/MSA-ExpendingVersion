package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.TrendingProductMapper;
import com.market.MSA.models.Product;
import com.market.MSA.models.TrendingProduct;
import com.market.MSA.repositories.FeedbackRepository;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.repositories.TrendingProductRepository;
import com.market.MSA.requests.TrendingProductRequest;
import com.market.MSA.responses.TrendingProductResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrendingProductService {
  TrendingProductRepository trendingProductRepository;
  TrendingProductMapper trendingProductMapper;
  ProductRepository productRepository;
  FeedbackRepository feedbackRepository;

  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  public void createTrendingProductData() {
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
                  int sales = product.getSales();
                  double trendScore = calculateTrendScore(avgRating, sales);

                  return new TrendingProduct(0, trendScore, currentDate, product);
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

  double calculateTrendScore(double avgRating, int sales) {
    double weightRating = 0.3;
    double weightSales = 0.7;
    return (avgRating * weightRating) + (sales * weightSales);
  }

  // Tạo TrendingProduct
  @Transactional
  public TrendingProductResponse createTrendingProduct(TrendingProductRequest request) {
    TrendingProduct trendingProduct = trendingProductMapper.toTrendingProduct(request);
    TrendingProduct savedTrendingProduct = trendingProductRepository.save(trendingProduct);
    return trendingProductMapper.toTrendingProductResponse(savedTrendingProduct);
  }

  // Cập nhật TrendingProduct
  @Transactional
  public TrendingProductResponse updateTrendingProduct(Long id, TrendingProductRequest request) {
    TrendingProduct trendingProduct =
        trendingProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.TRENDING_PRODUCT_NOT_FOUND));

    trendingProductMapper.updateTrendingProductFromRequest(request, trendingProduct);
    TrendingProduct updatedTrendingProduct = trendingProductRepository.save(trendingProduct);
    return trendingProductMapper.toTrendingProductResponse(updatedTrendingProduct);
  }

  // Xóa TrendingProduct
  @Transactional
  public void deleteTrendingProduct(Long id) {
    if (!trendingProductRepository.existsById(id)) {
      throw new AppException(ErrorCode.TRENDING_PRODUCT_NOT_FOUND);
    }
    trendingProductRepository.deleteById(id);
  }

  // Lấy TrendingProduct theo ID
  public TrendingProductResponse getTrendingProductById(Long id) {
    TrendingProduct trendingProduct =
        trendingProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.TRENDING_PRODUCT_NOT_FOUND));
    return trendingProductMapper.toTrendingProductResponse(trendingProduct);
  }

  // Lấy tất cả TrendingProduct (phân trang)
  public List<TrendingProductResponse> getAllTrendingProducts(int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize); // Page bắt đầu từ 0
    return trendingProductRepository.findAll(pageable).stream()
        .map(trendingProductMapper::toTrendingProductResponse)
        .collect(Collectors.toList());
  }
}
