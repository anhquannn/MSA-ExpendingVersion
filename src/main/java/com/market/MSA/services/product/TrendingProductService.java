package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.TrendingProductMapper;
import com.market.MSA.models.product.TrendingProduct;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.TrendingProductRepository;
import com.market.MSA.requests.product.TrendingProductRequest;
import com.market.MSA.responses.product.TrendingProductResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrendingProductService {
  final TrendingProductRepository trendingProductRepository;
  final TrendingProductMapper trendingProductMapper;
  final EntityFinderService entityFinderService;
  final ProductRepository productRepository;

  // Tạo TrendingProduct
  @Transactional
  public TrendingProductResponse createTrendingProduct(TrendingProductRequest request) {
    TrendingProduct trendingProduct = trendingProductMapper.toTrendingProduct(request);
    trendingProduct.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

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
    trendingProduct.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

    trendingProductMapper.updateTrendingProductFromRequest(request, trendingProduct);
    TrendingProduct updatedTrendingProduct = trendingProductRepository.save(trendingProduct);
    return trendingProductMapper.toTrendingProductResponse(updatedTrendingProduct);
  }

  // Xóa TrendingProduct
  @Transactional
  public boolean deleteTrendingProduct(Long id) {
    if (!trendingProductRepository.existsById(id)) {
      throw new AppException(ErrorCode.TRENDING_PRODUCT_NOT_FOUND);
    }
    trendingProductRepository.deleteById(id);
    return true;
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
