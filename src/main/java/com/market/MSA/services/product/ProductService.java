package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.InventoryProductMapper;
import com.market.MSA.mappers.product.ProductMapper;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.product.*;
import com.market.MSA.requests.filters.ProductFilterRequest;
import com.market.MSA.requests.product.ProductRequest;
import com.market.MSA.responses.product.InventoryProductResponse;
import com.market.MSA.responses.product.ProductFilterResponse;
import com.market.MSA.responses.product.ProductResponse;
import com.market.MSA.services.others.EntityFinderService;
import com.market.MSA.services.others.NotificationService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
  final InventoryRepository inventoryRepository;
  final InventoryProductRepository inventoryProductRepository;
  final InventoryProductMapper inventoryProductMapper;
  final InventoryProductService inventoryProductService;
  static final String DEFAULT_SORT_BY = "price";
  static final String DEFAULT_SORT_DIRECTION = "asc";

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "filtered_products", allEntries = true),
        @CacheEvict(value = "branch_products", allEntries = true)
      })
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
  @Caching(
      evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "filtered_products", allEntries = true),
        @CacheEvict(value = "branch_products", allEntries = true)
      })
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
  @Caching(
      evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "filtered_products", allEntries = true),
        @CacheEvict(value = "branch_products", allEntries = true)
      })
  public boolean deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    productRepository.deleteById(id);
    return true;
  }

  public ProductResponse getProductById(Long id) {
    Product product = findProductEntityById(id);
    return productMapper.toProductResponse(product);
  }

  public Product findProductById(Long id) {
    return findProductEntityById(id);
  }

  private Product findProductEntityById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "filtered_products", allEntries = true),
        @CacheEvict(value = "branch_products", allEntries = true)
      })
  public void updateTotalRevenue(Long productId, int quantity) {
    Product product = findProductEntityById(productId);
    product.setTotalRevenue(product.getTotalRevenue() + quantity);
    productRepository.save(product);
  }

  @Cacheable(value = "products", key = "'all_' + #page + '_' + #pageSize")
  public List<ProductResponse> getAllProducts(int page, int pageSize) {
    return productRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(productMapper::toProductResponse)
        .collect(Collectors.toList());
  }

  @Cacheable(
      value = "filtered_products",
      key =
          "{#request.branchId, #request.categoryId, #request.supplierId, #request.unit, #request.netWeight, #request.minPrice, #request.maxPrice, #request.keyword, #request.page, #request.pageSize, #request.sortBy, #request.sortDirection}")
  public ProductFilterResponse filterProducts(ProductFilterRequest request) {
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

    // Check if branchId is provided to handle discounted products
    if (request.getBranchId() != null) {
      // First, get the inventory for the branch
      Inventory inventory =
          inventoryRepository
              .findByBranch_BranchId(request.getBranchId())
              .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

      // Get paginated discounted products for this branch
      int discountedPageSize = request.getPageSize() * 2; // Show more discounted items

      // Create a separate sort for discounted products to handle field name differences
      Sort discountedSort = Sort.by(direction, sortBy.equals("price") ? "currentPrice" : sortBy);

      Pageable discountedPageable =
          PageRequest.of(
              request.getPage() - 1, // Align page numbers with regular products
              discountedPageSize,
              discountedSort);

      // Get paginated discounted products
      Page<InventoryProduct> discountedPage =
          inventoryProductRepository.findByInventory_InventoryIdAndIsDiscountedTrue(
              inventory.getInventoryId(), discountedPageable);

      // Get the list of discounted product IDs to exclude from regular products
      List<Long> discountedProductIds =
          discountedPage.getContent().stream()
              .map(ip -> ip.getProduct().getProductId())
              .distinct()
              .collect(Collectors.toList());

      // Map the discounted inventory products to responses with pagination
      Page<InventoryProductResponse> discountedProductsPage =
          discountedPage.map(inventoryProductMapper::toInventoryProductResponse);

      // Get filtered products excluding the discounted ones
      Page<Product> products =
          productRepository.filterWithPaging(
              request.getBranchId(),
              request.getCategoryId(),
              request.getSupplierId(),
              request.getUnit(),
              request.getNetWeight(),
              request.getMinPrice(),
              request.getMaxPrice(),
              request.getFromDate(),
              request.getToDate(),
              request.getKeyword(),
              discountedProductIds, // Pass the list of discounted product IDs to exclude
              pageable);

      // Return both regular and discounted products with pagination
      return ProductFilterResponse.fromPages(
          products.map(
              prod -> {
                ProductResponse resp = productMapper.toProductResponse(prod);
                double curPrice =
                    inventoryProductService.getBranchCurrentPrice(
                        request.getBranchId(), prod.getProductId());
                resp.setBranchCurrentPrice(curPrice);
                return resp;
              }),
          discountedProductsPage);
    } else {
      // If no branchId is provided, just return the regular filtered products
      Page<Product> products =
          productRepository.filterWithPaging(
              null, // branchId
              request.getCategoryId(),
              request.getSupplierId(),
              request.getUnit(),
              request.getNetWeight(),
              request.getMinPrice(),
              request.getMaxPrice(),
              request.getFromDate(),
              request.getToDate(),
              request.getKeyword(),
              Collections.emptyList(), // No products to exclude
              pageable);

      // Create an empty page for discounted products
      Page<InventoryProductResponse> emptyDiscountedPage =
          new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, 0);

      return ProductFilterResponse.fromPages(
          products.map(productMapper::toProductResponse), emptyDiscountedPage);
    }
  }

  @Cacheable(value = "branch_products", key = "{#branchId, #page, #size, #sortBy, #sortDirection}")
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
