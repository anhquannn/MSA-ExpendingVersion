package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.ProductMapper;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.product.CategoryRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.SupplierRepository;
import com.market.MSA.requests.product.ProductFilterRequest;
import com.market.MSA.requests.product.ProductRequest;
import com.market.MSA.responses.product.ProductResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.others.NotificationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
  final EntityFinderService entityFinderService;
  final ProductRepository productRepository;
  final SupplierRepository supplierRepository;
  final CategoryRepository categoryRepository;
  final ProductMapper productMapper;
  final NotificationService notificationService;
  static final String DEFAULT_SORT_BY = "price";
  static final String DEFAULT_SORT_DIRECTION = "asc";

  @Transactional
  public ProductResponse createProduct(ProductRequest request, boolean sendNotificationToAll) {
    Product product = productMapper.toProduct(request);
    product.setSupplier(
        entityFinderService.findByIdOrThrow(
            supplierRepository, request.getSupplierId(), ErrorCode.SUPPLIER_NOT_FOUND));
    product.setCategory(
        entityFinderService.findByIdOrThrow(
            categoryRepository, request.getCategoryId(), ErrorCode.CATEGORY_NOT_FOUND));

    Product savedProduct = productRepository.save(product);

    if (sendNotificationToAll) {
      notificationService.sendProductNotificationToAllCustomers(savedProduct.getProductId(), true);
    }

    return productMapper.toProductResponse(savedProduct);
  }

  @Transactional
  public ProductResponse updateProduct(Long id, ProductRequest request) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    product.setSupplier(
        entityFinderService.findByIdOrThrow(
            supplierRepository, request.getSupplierId(), ErrorCode.SUPPLIER_NOT_FOUND));
    product.setCategory(
        entityFinderService.findByIdOrThrow(
            categoryRepository, request.getCategoryId(), ErrorCode.CATEGORY_NOT_FOUND));

    productMapper.updateProductFromRequest(request, product);
    Product updatedProduct = productRepository.save(product);
    return productMapper.toProductResponse(updatedProduct);
  }

  @Transactional
  public boolean deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    productRepository.deleteById(id);
    return true;
  }

  // @Cacheable(value = "products", key = "#id")
  public ProductResponse getProductById(Long id) {
    Product product = findProductEntityById(id);
    return productMapper.toProductResponse(product);
  }

  // @Cacheable(value = "products", key = "#id")
  public Product findProductById(Long id) {
    return findProductEntityById(id);
  }

  private Product findProductEntityById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  // @CacheEvict(value = "products", key = "#productId")
  @Transactional
  public void updateTotalRevenue(Long productId, int quantity) {
    Product product = findProductEntityById(productId);
    product.setTotalRevenue(product.getTotalRevenue() + quantity);
    productRepository.save(product);
  }

  // @Cacheable(value = "products", key = "'all_' + #page + '_' + #pageSize")
  public List<ProductResponse> getAllProducts(int page, int pageSize) {
    return productRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
  }

  public Page<ProductResponse> filterProducts(ProductFilterRequest request) {
    // Set default values for pagination and sorting
    String sortBy =
        (request.getSortBy() != null && !request.getSortBy().isEmpty())
            ? request.getSortBy()
            : DEFAULT_SORT_BY;

    String sortDirection =
        (request.getSortDirection() != null && !request.getSortDirection().isEmpty())
            ? request.getSortDirection()
            : DEFAULT_SORT_DIRECTION;

    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable =
        PageRequest.of(
            request.getPage() - 1, // Page numbers are 0-based in Spring
            request.getPageSize(),
            Sort.by(direction, sortBy));

    // Call repository with all filters
    Page<Product> products =
        productRepository.findFilteredProducts(
            request.getBranchId(),
            request.getCategoryId(),
            request.getSupplierId(),
            request.getUnit(),
            request.getNetWeight(),
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getKeyword(),
            pageable);

    // Convert to DTO and return
    return products.map(productMapper::toProductResponse);
  }

  //  @Cacheable(
  //      value = "products",
  //      key =
  //          "'branch_all_' + #branchId + '_' + #page + '_' + #size + '_' + #sortBy + '_' +
  // #sortDirection")
  public Page<ProductResponse> getAllProductsInBranch(
      Long branchId, int page, int size, String sortBy, String sortDirection) {

    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    // Get all products in branch with pagination using database query
    Page<Product> products =
        productRepository.findByBranchAndFilters(branchId, null, null, null, pageable);

    // Convert to response DTOs
    return products.map(productMapper::toProductResponse);
  }
}
