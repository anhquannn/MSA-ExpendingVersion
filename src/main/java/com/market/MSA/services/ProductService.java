package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.ProductMapper;
import com.market.MSA.models.Product;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.requests.ProductRequest;
import com.market.MSA.responses.ProductResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  // Tạo sản phẩm
  public ProductResponse createProduct(ProductRequest request) {
    Product product = productMapper.toProduct(request);
    Product savedProduct = productRepository.save(product);
    return productMapper.toProductResponse(savedProduct);
  }

  // Cập nhật sản phẩm
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    productMapper.updateProductFromRequest(request, product);
    Product updatedProduct = productRepository.save(product);
    return productMapper.toProductResponse(updatedProduct);
  }

  // Xóa sản phẩm
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    productRepository.deleteById(id);
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

  // Cập nhật số lượng tồn kho
  @Transactional
  public void updateStockNumber(Long productId, int quantity) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    if (product.getStockNumber() < quantity) {
      throw new RuntimeException("Insufficient stock");
    }

    product.setStockNumber(product.getStockNumber() - quantity);
    product.setSales(product.getSales() + quantity);

    updateStockLevel(product);
    productRepository.save(product);
  }

  // Khôi phục số lượng tồn kho
  @Transactional
  public void restoreStock(Long productId, int quantity) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    product.setStockNumber(product.getStockNumber() + quantity);
    product.setSales(product.getSales() - quantity);

    updateStockLevel(product);
    productRepository.save(product);
  }

  // Tìm kiếm sản phẩm theo tên
  public List<ProductResponse> searchProductsByName(String name, int page, int pageSize) {
    return productRepository.findAll().stream()
        .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
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
            (p1, p2) -> Integer.compare(p2.getSales(), p1.getSales())) // Sắp xếp theo số lượng bán
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
  }

  // Cập nhật mức tồn kho
  private void updateStockLevel(Product product) {
    if (product.getStockNumber() > 300) {
      product.setStockLevel("high");
    } else if (product.getStockNumber() > 50) {
      product.setStockLevel("medium");
    } else {
      product.setStockLevel("low");
    }
  }
}
