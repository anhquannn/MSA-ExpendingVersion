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
import java.util.Comparator;
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
  final InventoryService inventoryService;

  @Transactional
  public InventoryProductResponse createInventoryProduct(InventoryProductRequest request) {
    // Lấy thông tin kho và sản phẩm từ CSDL.
    Inventory inventory =
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_NOT_FOUND);
    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND);

    // Kiểm tra xem sản phẩm này đã tồn tại trong kho này chưa.
    List<InventoryProduct> existingProducts =
        inventoryProductRepository.filter(
            product.getProductId(), inventory.getInventoryId(), null, true, null, null, null, null);

    if (!existingProducts.isEmpty()) {
      // Nếu đã tồn tại, kiểm tra xem có bản ghi nào còn hàng không.
      boolean hasStock = existingProducts.stream().anyMatch(ip -> ip.getStockNumber() > 0);

      if (hasStock) {
        // Nếu còn hàng, không cho phép tạo mới để tránh trùng lặp, yêu cầu cập nhật bản ghi cũ.
        throw new AppException(ErrorCode.INVENTORY_PRODUCT_EXISTS_WITH_STOCK);
      }

      // Nếu không còn hàng, cho phép cập nhật bản ghi cũ với thông tin mới (nhập hàng mới).
      InventoryProduct existingProduct = existingProducts.getFirst();
      existingProduct.setStockNumber(request.getStockNumber());
      existingProduct.setExpDate(request.getExpDate());
      existingProduct.setCurrentPrice(product.getPrice());
      existingProduct.setDiscounted(false);
      existingProduct.setActive(true);

      updateStockLevel(existingProduct); // Cập nhật lại mức tồn kho.

      return inventoryProductMapper.toInventoryProductResponse(
          inventoryProductRepository.save(existingProduct));
    }

    // Nếu chưa tồn tại, tạo mới một bản ghi InventoryProduct.
    InventoryProduct inventoryProduct =
        InventoryProduct.builder()
            .inventory(inventory)
            .product(product)
            .stockNumber(request.getStockNumber())
            .stockLevel(calculateStockLevel(request.getStockNumber())) // Tính mức tồn kho
            .expDate(request.getExpDate())
            .currentPrice(product.getPrice()) // Lấy giá gốc của sản phẩm
            .isActive(true)
            .isDiscounted(false)
            .build();

    InventoryProduct savedProduct = inventoryProductRepository.save(inventoryProduct);
    return inventoryProductMapper.toInventoryProductResponse(savedProduct);
  }

  @Transactional
  public InventoryProductResponse updateInventoryProduct(Long id, InventoryProductRequest request) {
    InventoryProduct inventoryProduct =
        inventoryProductRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND));

    // Lấy thông tin kho và sản phẩm mới (nếu có thay đổi).
    Inventory newInventory =
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_NOT_FOUND);
    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND);

    // Cập nhật các trường thông tin.
    inventoryProduct.setInventory(newInventory);
    inventoryProduct.setProduct(product);
    inventoryProduct.setStockNumber(request.getStockNumber());
    inventoryProduct.setStockLevel(request.getStockLevel());

    Integer stockNumberChecked = request.getStockNumberChecked();

    // Logic này dùng cho việc kiểm kê kho:
    // Nếu số lượng kiểm kê thực tế (stockNumberChecked) được cung cấp.
    if (stockNumberChecked != null) {
      int stockNumber = request.getStockNumber(); // Số lượng trên hệ thống
      // Tính toán và ghi nhận chênh lệch.
      int stockNumberDifferent = Math.abs(stockNumber - stockNumberChecked);
      inventoryProduct.setStockNumberChecked(stockNumberChecked);
      inventoryProduct.setStockNumberDifferent(stockNumberDifferent);
    }

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
      return "LOW";
    } else if (stockNumber <= 300) {
      return "MEDIUM";
    } else {
      return "HIGH";
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
    Long branchId = order.getBranch().getBranchId();

    // Duyệt qua từng sản phẩm trong đơn hàng.
    for (OrderDetail orderDetail : order.getOrderDetails()) {
      Long productId = orderDetail.getProduct().getProductId();
      int quantity = orderDetail.getQuantity();

      // Tìm kho của chi nhánh.
      Inventory inventory =
          inventoryRepository
              .findByBranch_BranchId(branchId)
              .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

      // Tìm sản phẩm trong kho đó.
      List<InventoryProduct> inventoryProducts =
          inventoryProductRepository.filter(
              productId, inventory.getInventoryId(), null, null, null, null, null, null);

      if (inventoryProducts.isEmpty()) {
        throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
      }

      // Cộng trả lại số lượng đã hủy vào tồn kho.
      InventoryProduct inventoryProduct = inventoryProducts.getFirst();
      inventoryProduct.setStockNumber(inventoryProduct.getStockNumber() + quantity);
      updateStockLevel(inventoryProduct);
      inventoryProductRepository.save(inventoryProduct);

      // Giảm doanh thu đã ghi nhận cho sản phẩm.
      Product product =
          productRepository
              .findById(productId)
              .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
      product.setTotalRevenue(product.getTotalRevenue() - quantity);
      productRepository.save(product);

      // Giảm tổng doanh thu trong kho.
      inventoryService.updateTotalRevenue(inventory.getInventoryId(), -quantity);
    }
  }

  @Transactional
  public void updateInventoryProduct(Long branchId, Long productId, int quantity) {
    // Tìm kho của chi nhánh.
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Tìm sản phẩm trong kho.
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.filter(
            productId, inventory.getInventoryId(), null, null, null, null, null, null);

    if (inventoryProducts.isEmpty()) {
      throw new AppException(ErrorCode.INVENTORY_PRODUCT_NOT_FOUND);
    }
    InventoryProduct inventoryProduct = inventoryProducts.getFirst();

    // Kiểm tra xem có đủ hàng để bán không.
    if (inventoryProduct.getStockNumber() < quantity) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
    }

    // Trừ số lượng đã bán khỏi tồn kho.
    inventoryProduct.setStockNumber(inventoryProduct.getStockNumber() - quantity);
    updateStockLevel(inventoryProduct);
    inventoryProductRepository.save(inventoryProduct);

    // Tăng tổng doanh thu trong kho.
    inventoryService.updateTotalRevenue(inventory.getInventoryId(), quantity);

    // Tăng tổng doanh thu cho sản phẩm.
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    product.setTotalRevenue(product.getTotalRevenue() + quantity);
    productRepository.save(product);
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

  public boolean checkStockAvailability(Long branchId, Long productId, int quantity) {
    Integer totalStock =
        inventoryProductRepository.getTotalStockByBranchAndProduct(branchId, productId);
    return totalStock != null && totalStock >= quantity;
  }

  @Cacheable("all_inventory_products")
  public List<InventoryProductResponse> getAll() {
    return inventoryProductRepository.findAll().stream()
        .map(inventoryProductMapper::toInventoryProductResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("inventory_products_paging")
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

  @Cacheable("inventory_products_list")
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

  public double getBranchCurrentPrice(Long branchId, Long productId) {
    // Lấy inventory của chi nhánh
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Ưu tiên lấy giá ở InventoryProduct (ví dụ hàng giảm giá, cận date, v.v.)
    List<InventoryProduct> inventoryProducts =
        inventoryProductRepository.filter(
            productId, inventory.getInventoryId(), null, true, null, null, null, null);

    return inventoryProducts.stream()
        // Chỉ xét các sản phẩm còn tồn kho
        .filter(ip -> ip.getStockNumber() > 0)
        // Sắp xếp theo currentPrice tăng dần để lấy giá tốt nhất cho khách
        .sorted(Comparator.comparingDouble(InventoryProduct::getCurrentPrice))
        .map(InventoryProduct::getCurrentPrice)
        .findFirst()
        // Nếu không có bản ghi tồn kho phù hợp thì fallback về giá gốc của Product
        .orElseGet(
            () ->
                productRepository
                    .findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND))
                    .getPrice());
  }
}
