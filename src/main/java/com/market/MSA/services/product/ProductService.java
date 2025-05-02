package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.ProductMapper;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.product.CategoryRepository;
import com.market.MSA.repositories.product.ManufacturerRepository;
import com.market.MSA.repositories.product.ProductRepository;
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
import org.springframework.data.domain.PageImpl;
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
  final ManufacturerRepository manufacturerRepository;
  final CategoryRepository categoryRepository;
  final ProductMapper productMapper;
  final NotificationService notificationService;

  @Transactional
  public ProductResponse createProduct(ProductRequest request, boolean sendNotificationToAll) {
    Product product = productMapper.toProduct(request);
    product.setManufacturer(
        entityFinderService.findByIdOrThrow(
            manufacturerRepository, request.getManufactureId(), ErrorCode.MANUFACTURER_NOT_FOUND));
    product.setCategory(
        entityFinderService.findByIdOrThrow(
            categoryRepository, request.getCategoryId(), ErrorCode.CATEGORY_NOT_FOUND));
    product.setCurrentPrice(request.getPrice());

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

  //  @Cacheable(
  //      value = "products",
  //      key =
  //          "'filtered_' + #size + '_' + #minPrice + '_' + #maxPrice + '_' + #color + '_' +
  // #categoryId + '_' +
  // #page + '_' + #pageSize")
  public List<ProductResponse> filterAndSortProducts(
      double minPrice, double maxPrice, String color, Long categoryId, int page, int pageSize) {
    return productRepository.findAll().stream()
        .filter(p -> (minPrice <= 0 || p.getPrice() >= minPrice))
        .filter(p -> (maxPrice <= 0 || p.getPrice() <= maxPrice))
        .filter(p -> (color == null || color.isEmpty() || p.getColor().equalsIgnoreCase(color)))
        .filter(
            p ->
                (categoryId == null
                    || categoryId == 0
                    || p.getCategory().getCategoryId().equals(categoryId)))
        .sorted(
            (p1, p2) ->
                Double.compare(p2.getTotalRevenue(), p1.getTotalRevenue())) // Sắp xếp theo số lượng
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
  }

  //  @Cacheable(
  //      value = "products",
  //      key =
  //          "'branch_' + #branchId + '_' + #keyword + '_' + #minPrice + '_' + #maxPrice + '_' +
  // #color + '_' + #size
  // + '_' + #page + '_' + #pageSize + '_' + #sortBy + '_' + #sortDirection")
  public Page<ProductResponse> searchProductsInBranch(
      Long branchId,
      String keyword,
      Double minPrice,
      Double maxPrice,
      String color,
      int page,
      int pageSize,
      String sortBy,
      String sortDirection) {

    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

    // Get filtered products with pagination using searchByKeyword
    Page<Product> products = productRepository.searchByKeyword(branchId, keyword, pageable);

    // Apply additional filters
    List<Product> filteredProducts =
        products.getContent().stream()
            .filter(
                product -> {
                  boolean matchesPrice =
                      (minPrice == null || product.getCurrentPrice() >= minPrice)
                          && (maxPrice == null || product.getCurrentPrice() <= maxPrice);
                  boolean matchesColor =
                      color == null
                          || color.isEmpty()
                          || product.getColor().equalsIgnoreCase(color);
                  return matchesPrice && matchesColor;
                })
            .collect(Collectors.toList());

    // Create new page with filtered results
    Page<Product> filteredPage =
        new PageImpl<>(filteredProducts, pageable, filteredProducts.size());

    // Convert to response DTOs
    return filteredPage.map(productMapper::toProductResponse);
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
        productRepository.findByBranchAndFilters(branchId, null, null, null, null, pageable);

    // Convert to response DTOs
    return products.map(productMapper::toProductResponse);
  }

  //  @Cacheable(
  //      value = "products",
  //      key =
  //          "'search_' + #keyword + '_' + #minPrice + '_' + #maxPrice + '_' + #color + '_' + #size
  // + '_' +
  // #categoryId + '_' + #manufacturerId + '_' + #page + '_' + #pageSize + '_' + #sortBy + '_' +
  // #sortDirection")
  public Page<ProductResponse> searchProducts(
      String keyword,
      Double minPrice,
      Double maxPrice,
      String color,
      Long categoryId,
      Long manufacturerId,
      int page,
      int pageSize,
      String sortBy,
      String sortDirection) {

    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

    // Get products with filters and pagination
    Page<Product> products =
        productRepository.searchProducts(
            keyword, minPrice, maxPrice, color, categoryId, manufacturerId, pageable);

    // Convert to response DTOs
    return products.map(productMapper::toProductResponse);
  }
}
