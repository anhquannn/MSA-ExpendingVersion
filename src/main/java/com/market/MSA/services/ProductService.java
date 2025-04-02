package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.ProductMapper;
import com.market.MSA.models.Product;
import com.market.MSA.repositories.CategoryRepository;
import com.market.MSA.repositories.ManufacturerRepository;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.requests.ProductRequest;
import com.market.MSA.responses.ProductResponse;
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
public class ProductService {
  final EntityFinderService entityFinderService;

  final ProductRepository productRepository;
  final ManufacturerRepository manufacturerRepository;
  final CategoryRepository categoryRepository;

  final ProductMapper productMapper;

  // Tạo sản phẩm
  @Transactional
  public ProductResponse createProduct(ProductRequest request) {
    Product product = productMapper.toProduct(request);
    product.setManufacturer(
        entityFinderService.findByIdOrThrow(
            manufacturerRepository, request.getManufactureId(), ErrorCode.MANUFACTURER_NOT_FOUND));
    product.setCategory(
        entityFinderService.findByIdOrThrow(
            categoryRepository, request.getCategoryId(), ErrorCode.CATEGORY_NOT_FOUND));

    Product savedProduct = productRepository.save(product);
    return productMapper.toProductResponse(savedProduct);
  }

  // Cập nhật sản phẩm
  @Transactional
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    product.setManufacturer(
        entityFinderService.findByIdOrThrow(
            manufacturerRepository, request.getManufactureId(), ErrorCode.MANUFACTURER_NOT_FOUND));
    product.setCategory(
        entityFinderService.findByIdOrThrow(
            categoryRepository, request.getCategoryId(), ErrorCode.CATEGORY_NOT_FOUND));

    productMapper.updateProductFromRequest(request, product);
    Product updatedProduct = productRepository.save(product);
    return productMapper.toProductResponse(updatedProduct);
  }

  // Xóa sản phẩm
  @Transactional
  public boolean deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    productRepository.deleteById(id);
    return true;
  }

  // Lấy sản phẩm theo ID
  public ProductResponse getProductById(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    return productMapper.toProductResponse(product);
  }

  public Product findProductById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  // Lấy tất cả sản phẩm (phân trang)
  public List<ProductResponse> getAllProducts(int page, int pageSize) {
    return productRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
  }

  // Tìm kiếm sản phẩm theo tên
  public List<ProductResponse> searchProductsByName(String keyword, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize); // Tạo Pageable đúng cách
    List<Product> products = productRepository.searchByKeyword(keyword, pageable);

    return products.stream().map(productMapper::toProductResponse).collect(Collectors.toList());
  }

  // Lọc & sắp xếp sản phẩm
  public List<ProductResponse> filterAndSortProducts(
      int size,
      double minPrice,
      double maxPrice,
      String color,
      Long categoryId,
      int page,
      int pageSize) {
    return productRepository.findAll().stream()
        .filter(p -> (size <= 0 || p.getSize() == size))
        .filter(p -> (minPrice <= 0 || p.getPrice() >= minPrice))
        .filter(p -> (maxPrice <= 0 || p.getPrice() <= maxPrice))
        .filter(p -> (color == null || color.isEmpty() || p.getColor().equalsIgnoreCase(color)))
        .filter(
            p ->
                (categoryId == null
                    || categoryId == 0
                    || p.getCategory().getCategoryId() == categoryId))
        .sorted(
            (p1, p2) ->
                Long.compare(p2.getTotalRevenue(), p1.getTotalRevenue())) // Sắp xếp theo số lượng
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
  }
}
