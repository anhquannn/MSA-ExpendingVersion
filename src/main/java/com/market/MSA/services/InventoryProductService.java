package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.InventoryProductMapper;
import com.market.MSA.models.Inventory;
import com.market.MSA.models.InventoryProduct;
import com.market.MSA.models.Order;
import com.market.MSA.models.OrderDetail;
import com.market.MSA.repositories.InventoryProductRepository;
import com.market.MSA.repositories.InventoryRepository;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.requests.InventoryProductRequest;
import com.market.MSA.responses.InventoryProductResponse;
import com.market.MSA.responses.InventoryStatisticsResponse;
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
public class InventoryProductService {
  final InventoryProductRepository inventoryProductRepository;
  final EntityFinderService entityFinderService;
  final InventoryRepository inventoryRepository;
  final InventoryProductMapper inventoryProductMapper;
  private final ProductRepository productRepository;
  private final ProductService productService;
  private final InventoryService inventoryService;

  @Transactional
  public InventoryProductResponse createInventoryProduct(InventoryProductRequest request) {
    InventoryProduct inventoryProduct = inventoryProductMapper.toInventoryProduct(request);
    inventoryProduct.setInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    inventoryProduct.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

    inventoryProductRepository.save(inventoryProduct);
    return inventoryProductMapper.toInventoryProductResponse(inventoryProduct);
  }

  @Transactional
  public InventoryProductResponse updateInventoryProduct(Long id, InventoryProductRequest request) {
    InventoryProduct inventoryProduct =
        inventoryProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));
    inventoryProduct.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    inventoryProduct.setInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

    inventoryProductMapper.updateInventoryProductFromRequest(request, inventoryProduct);
    InventoryProduct inventoryProductUpdated = inventoryProductRepository.save(inventoryProduct);

    return inventoryProductMapper.toInventoryProductResponse(inventoryProductUpdated);
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
        inventoryProductRepository.findByInventory_InventoryId(inventoryId, pageable);
    return inventoryProducts.stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }

  public int getTotalStockInBranch(Long branchId, Long productId) {
    // Find the inventory for the branch
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Find inventory products for this inventory and product
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.findByInventory_InventoryIdAndProduct_ProductId(
            inventory.getInventoryId(), productId);

    return inventoryProducts.stream().mapToInt(InventoryProduct::getStockNumber).sum();
  }

  private void updateStockLevel(InventoryProduct inventoryProduct) {
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

  /** Lấy danh sách sản phẩm trong kho của một chi nhánh có phân trang */
  public Page<InventoryProductResponse> getInventoryProductsByBranch(
      Long branchId, int page, int pageSize, String sortBy, String sortDirection) {
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
    Pageable pageable = PageRequest.of(page, pageSize, sort);

    // Lấy danh sách sản phẩm có phân trang
    Page<InventoryProduct> inventoryProductsPage =
        inventoryProductRepository.findByInventory_InventoryId(
            inventory.getInventoryId(), pageable);

    // Chuyển đổi sang response
    return inventoryProductsPage.map(inventoryProductMapper::toInventoryProductResponse);
  }

  /**
   * Kiểm tra xem có đủ số lượng tồn kho cho sản phẩm trong chi nhánh không
   *
   * @param branchId ID của chi nhánh
   * @param productId ID của sản phẩm
   * @param quantity Số lượng cần kiểm tra
   * @return true nếu đủ số lượng, false nếu không đủ
   */
  public boolean checkStockAvailability(Long branchId, Long productId, int quantity) {
    Integer totalStock =
        inventoryProductRepository.getTotalStockByBranchAndProduct(branchId, productId);
    return totalStock != null && totalStock >= quantity;
  }
}
