package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.InventoryProductMapper;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Product;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.requests.filters.InventoryProductFilterRequest;
import com.market.MSA.requests.product.InventoryProductRequest;
import com.market.MSA.responses.product.InventoryProductResponse;
import com.market.MSA.responses.product.InventoryStatisticsResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
public class InventoryProductService {
  final InventoryProductRepository inventoryProductRepository;
  final EntityFinderService entityFinderService;
  final InventoryRepository inventoryRepository;
  final InventoryProductMapper inventoryProductMapper;
  final ProductRepository productRepository;
  final ProductService productService;
  final InventoryService inventoryService;

  @Transactional
  public InventoryProductResponse createInventoryProduct(InventoryProductRequest request) {
    // Get the inventory and product
    Inventory inventory =
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_NOT_FOUND);
    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND);

    // Check if there's an existing inventory product for this product in the same inventory
    List<InventoryProduct> existingProducts =
        inventoryProductRepository.filter(
            inventory.getInventoryId(), product.getProductId(), null, null, null, null, null, null);

    if (!existingProducts.isEmpty()) {
      // Check if any existing product has remaining stock
      boolean hasStock = existingProducts.stream().anyMatch(ip -> ip.getStockNumber() > 0);

      if (hasStock) {
        throw new AppException(ErrorCode.INVENTORY_PRODUCT_EXISTS_WITH_STOCK);
      }

      // If no stock, we can update the existing record instead of creating a new one
      InventoryProduct existingProduct = existingProducts.getFirst();
      existingProduct.setStockNumber(request.getStockNumber());
      existingProduct.setExpDate(request.getExpDate());
      existingProduct.setCurrentPrice(product.getPrice());
      existingProduct.setDiscounted(false);
      existingProduct.setActive(true);

      updateStockLevel(existingProduct);

      return inventoryProductMapper.toInventoryProductResponse(
          inventoryProductRepository.save(existingProduct));
    }

    // Create new inventory product
    InventoryProduct inventoryProduct =
        InventoryProduct.builder()
            .inventory(inventory)
            .product(product)
            .stockNumber(request.getStockNumber())
            .stockLevel(calculateStockLevel(request.getStockNumber()))
            .expDate(request.getExpDate())
            .currentPrice(product.getPrice())
            .isActive(true)
            .isDiscounted(false)
            .build();

    InventoryProduct savedProduct = inventoryProductRepository.save(inventoryProduct);
    return inventoryProductMapper.toInventoryProductResponse(savedProduct);
  }

  @Transactional
  public InventoryProductResponse updateInventoryProduct(Long id, InventoryProductRequest request) {
    // Get existing inventory product
    InventoryProduct inventoryProduct =
        inventoryProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

    // Get the new inventory and product
    Inventory newInventory =
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_NOT_FOUND);
    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND);

    // Update inventory product
    inventoryProduct.setInventory(newInventory);
    inventoryProduct.setProduct(product);
    inventoryProduct.setStockNumber(request.getStockNumber());
    inventoryProduct.setStockLevel(request.getStockLevel());

    InventoryProduct updatedInventoryProduct = inventoryProductRepository.save(inventoryProduct);
    return inventoryProductMapper.toInventoryProductResponse(updatedInventoryProduct);
  }

  @Transactional
  public boolean deleteInventoryProduct(Long id) {
    if (!inventoryProductRepository.existsById(id)) {
      throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
    }
    inventoryProductRepository.deleteById(id);
    return true;
  }

  @Transactional
  public InventoryProductResponse getInventoryProductById(Long id) {
    InventoryProduct inventoryProduct =
        inventoryProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    return inventoryProductMapper.toInventoryProductResponse(inventoryProduct);
  }

//  @Cacheable(value = "inventory_products", key = "'stock_' + #branchId + '_' + #productId")
  public int getTotalStockInBranch(Long branchId, Long productId) {
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.filter(
            productId, inventory.getInventoryId(), null, null, null, null, null, null);

    return inventoryProducts.stream().mapToInt(InventoryProduct::getStockNumber).sum();
  }

  private String calculateStockLevel(int stockNumber) {
    if (stockNumber < 50) {
      return "low";
    } else if (stockNumber <= 300) {
      return "medium";
    } else {
      return "high";
    }
  }

  void updateStockLevel(InventoryProduct inventoryProduct) {
    inventoryProduct.setStockLevel(calculateStockLevel(inventoryProduct.getStockNumber()));

    // If stock reaches zero, mark as inactive
    if (inventoryProduct.getStockNumber() <= 0) {
      inventoryProduct.setActive(false);
    }
  }

  @Transactional
  public void restoreStock(Order order) {
    // Get branch ID from order
    Long branchId = order.getBranch().getBranchId();

    // Iterate through order details and restore stock for each product
    for (OrderDetail orderDetail : order.getOrderDetails()) {
      Long productId = orderDetail.getProduct().getProductId();
      int quantity = orderDetail.getQuantity();

      // Find the inventory for the branch
      Inventory inventory =
          inventoryRepository
              .findByBranch_BranchId(branchId)
              .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

      // Find inventory products for this inventory and product
      List<InventoryProduct> inventoryProducts =
          inventoryProductRepository.filter(
              productId, inventory.getInventoryId(), null, null, null, null, null, null);

      if (inventoryProducts.isEmpty()) {
        throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
      }

      // Restore stock to the first inventory product found
      InventoryProduct inventoryProduct = inventoryProducts.getFirst();
      inventoryProduct.setStockNumber(inventoryProduct.getStockNumber() + quantity);

      // Update stock level
      updateStockLevel(inventoryProduct);

      inventoryProductRepository.save(inventoryProduct);

      // Decrease total revenue in product
      productService.updateTotalRevenue(productId, -quantity);

      // Decrease total revenue in inventory
      inventoryService.updateTotalRevenue(inventory.getInventoryId(), -quantity);
    }
  }

  @Transactional
  public void updateInventoryProduct(Long branchId, Long productId, int quantity) {
    // Find the inventory for the branch
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Find inventory products for this inventory and product
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.filter(
            productId, inventory.getInventoryId(), null, null, null, null, null, null);

    if (inventoryProducts.isEmpty()) {
      throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
    }

    // Update stock for the first inventory product found
    InventoryProduct inventoryProduct = inventoryProducts.getFirst();

    // Check if there's enough stock
    if (inventoryProduct.getStockNumber() < quantity) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
    }

    // Deduct stock
    inventoryProduct.setStockNumber(inventoryProduct.getStockNumber() - quantity);

    // Update stock level
    updateStockLevel(inventoryProduct);

    inventoryProductRepository.save(inventoryProduct);

    // Update total revenue in inventory
    inventoryService.updateTotalRevenue(inventory.getInventoryId(), quantity);

    // Update total revenue in product
    productService.updateTotalRevenue(productId, quantity);
  }

  /** Lấy thống kê tổng quan về kho của một chi nhánh */
  public InventoryStatisticsResponse getInventoryStatistics(Long branchId) {
    // Tìm inventory của branch
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Sử dụng các query tối ưu để lấy thống kê
    int totalProducts =
        inventoryProductRepository.countProductsByInventoryId(inventory.getInventoryId());
    int totalQuantity =
        inventoryProductRepository.sumStockByInventoryId(inventory.getInventoryId());
    int lowStockCount =
        inventoryProductRepository.countLowStockByInventoryId(inventory.getInventoryId());
    int highStockCount =
        inventoryProductRepository.countHighStockByInventoryId(inventory.getInventoryId());

    return InventoryStatisticsResponse.builder()
        .totalProducts(totalProducts)
        .totalQuantity(totalQuantity)
        .lowStockCount(lowStockCount)
        .highStockCount(highStockCount)
        .build();
  }

//  @Cacheable(
//      value = "inventory_products",
//      key = "'availability_' + #branchId + '_' + #productId + '_' + #quantity")
  public boolean checkStockAvailability(Long branchId, Long productId, int quantity) {
    Integer totalStock =
        inventoryProductRepository.getTotalStockByBranchAndProduct(branchId, productId);
    return totalStock != null && totalStock >= quantity;
  }

//  @Cacheable(value = "inventory_products", key = "#request.hashCode()")
  public List<InventoryProductResponse> getAllInventoryProducts(
      InventoryProductFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    return inventoryProductRepository
        .filter(
            request.getProductId(),
            request.getInventoryId(),
            request.getBatchNumber(),
            request.isActive(),
            request.isDiscounted(),
            request.getFromDate(),
            request.getToDate(),
            sort)
        .stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }

//  @Cacheable(value = "inventory_products", key = "'paged_' + #request.hashCode()")
  public Page<InventoryProductResponse> getAllInventoryProductsWithPaging(
      InventoryProductFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    // Create pageable with 0-based page number
    Pageable pageable =
        PageRequest.of(
            request.getPage() - 1, // Convert to 0-based page
            request.getPageSize(),
            sort);

    // Apply filters
    return inventoryProductRepository
        .filterWithPaging(
            request.getProductId(),
            request.getInventoryId(),
            request.getBatchNumber(),
            request.isActive(),
            request.isDiscounted(),
            request.getFromDate(),
            request.getToDate(),
            pageable)
        .map(inventoryProductMapper::toInventoryProductResponse);
  }
}
