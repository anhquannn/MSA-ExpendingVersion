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

    // Create new inventory product
    InventoryProduct inventoryProduct =
        InventoryProduct.builder()
            .inventory(inventory)
            .product(product)
            .stockNumber(request.getStockNumber())
            .stockLevel(request.getStockLevel())
            .build();

    InventoryProduct saveProduct = inventoryProductRepository.save(inventoryProduct);

    return inventoryProductMapper.toInventoryProductResponse(saveProduct);
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

  public List<InventoryProductResponse> getInventoryProductByProductId(Long productId) {
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.findByProductId_ProductId(productId);
    return inventoryProducts.stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }

  public List<InventoryProductResponse> getInventoryProductByInventoryId(
      Long inventoryId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    Page<InventoryProduct> inventoryProducts =
        inventoryProductRepository.findByInventory_InventoryIdWithPageable(inventoryId, pageable);
    return inventoryProducts.stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }

  @Cacheable(value = "inventory_products", key = "'stock_' + #branchId + '_' + #productId")
  public int getTotalStockInBranch(Long branchId, Long productId) {
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.findByInventory_InventoryIdAndProduct_ProductId(
            inventory.getInventoryId(), productId);

    return inventoryProducts.stream().mapToInt(InventoryProduct::getStockNumber).sum();
  }

  public void updateStockLevel(InventoryProduct inventoryProduct) {
    int stockNumber = inventoryProduct.getStockNumber();
    if (stockNumber < 50) {
      inventoryProduct.setStockLevel("low");
    } else if (stockNumber <= 300) {
      inventoryProduct.setStockLevel("medium");
    } else {
      inventoryProduct.setStockLevel("high");
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
          inventoryProductRepository.findByInventory_InventoryIdAndProduct_ProductId(
              inventory.getInventoryId(), productId);

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
        inventoryProductRepository.findByInventory_InventoryIdAndProduct_ProductId(
            inventory.getInventoryId(), productId);

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

  @Cacheable(
      value = "inventory_products",
      key =
          "'branch_' + #branchId + '_' + #page + '_' + #size + '_' + #sortBy + '_' + #sortDirection")
  public Page<InventoryProductResponse> getInventoryProductsByBranch(
      Long branchId, int page, int size, String sortBy, String sortDirection) {
    // Tìm inventory của branch
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Tạo Pageable với sắp xếp
    Sort sort =
        Sort.by(
            sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    // Lấy danh sách sản phẩm có phân trang
    Page<InventoryProduct> inventoryProductsPage =
        inventoryProductRepository.findByInventory_InventoryIdWithPageable(
            inventory.getInventoryId(), pageable);

    // Chuyển đổi sang response
    return inventoryProductsPage.map(inventoryProductMapper::toInventoryProductResponse);
  }

  //  @Cacheable(
  //      value = "inventory_products",
  //      key = "'availability_' + #branchId + '_' + #productId + '_' + #quantity")
  public boolean checkStockAvailability(Long branchId, Long productId, int quantity) {
    Integer totalStock =
        inventoryProductRepository.getTotalStockByBranchAndProduct(branchId, productId);
    return totalStock != null && totalStock >= quantity;
  }
}
