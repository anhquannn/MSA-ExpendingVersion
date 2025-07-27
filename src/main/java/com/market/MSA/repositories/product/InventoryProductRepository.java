package com.market.MSA.repositories.product;

import com.market.MSA.models.product.InventoryProduct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {
  @Query(
      "SELECT ip FROM InventoryProduct ip WHERE "
          + "(:productId IS NULL OR ip.product.productId = :productId) AND "
          + "(:inventoryId IS NULL OR ip.inventory.inventoryId = :inventoryId) AND "
          + "(:batchNumber IS NULL OR ip.batchNumber = :batchNumber) AND "
          + "(:isActive IS NULL OR ip.isActive = :isActive) AND "
          + "(:isDiscounted IS NULL OR ip.isDiscounted = :isDiscounted) AND "
          + "(:fromDate IS NULL OR ip.expDate >= :fromDate) AND "
          + "(:toDate IS NULL OR ip.expDate <= :toDate)")
  Page<InventoryProduct> filterWithPaging(
      @Param("productId") Long productId,
      @Param("inventoryId") Long inventoryId,
      @Param("batchNumber") String batchNumber,
      @Param("isActive") Boolean isActive,
      @Param("isDiscounted") Boolean isDiscounted,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT ip FROM InventoryProduct ip WHERE "
          + "(:productId IS NULL OR ip.product.productId = :productId) AND "
          + "(:inventoryId IS NULL OR ip.inventory.inventoryId = :inventoryId) AND "
          + "(:batchNumber IS NULL OR ip.batchNumber = :batchNumber) AND "
          + "(:isActive IS NULL OR ip.isActive = :isActive) AND "
          + "(:isDiscounted IS NULL OR ip.isDiscounted = :isDiscounted) AND "
          + "(:fromDate IS NULL OR ip.expDate >= :fromDate) AND "
          + "(:toDate IS NULL OR ip.expDate <= :toDate) AND "
          + "(:minStock IS NULL OR ip.stockNumber >= :minStock) AND "
          + "(:maxStock IS NULL OR ip.stockNumber <= :maxStock) AND "
          + "(:isLowStock IS NULL OR :isLowStock = false OR ip.stockNumber <= ip.minThreshold)")
  Page<InventoryProduct> filterWithPagingAndStock(
      @Param("productId") Long productId,
      @Param("inventoryId") Long inventoryId,
      @Param("batchNumber") String batchNumber,
      @Param("isActive") Boolean isActive,
      @Param("isDiscounted") Boolean isDiscounted,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("minStock") Double minStock,
      @Param("maxStock") Double maxStock,
      @Param("isLowStock") Boolean isLowStock,
      Pageable pageable);

  @Query(
      "SELECT ip FROM InventoryProduct ip WHERE "
          + "(:productId IS NULL OR ip.product.productId = :productId) AND "
          + "(:inventoryId IS NULL OR ip.inventory.inventoryId = :inventoryId) AND "
          + "(:batchNumber IS NULL OR ip.batchNumber = :batchNumber) AND "
          + "(:isActive IS NULL OR ip.isActive = :isActive) AND "
          + "(:isDiscounted IS NULL OR ip.isDiscounted = :isDiscounted) AND "
          + "(:fromDate IS NULL OR ip.expDate >= :fromDate) AND "
          + "(:toDate IS NULL OR ip.expDate <= :toDate)")
  List<InventoryProduct> filter(
      @Param("productId") Long productId,
      @Param("inventoryId") Long inventoryId,
      @Param("batchNumber") String batchNumber,
      @Param("isActive") Boolean isActive,
      @Param("isDiscounted") Boolean isDiscounted,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);

  @Query("SELECT COUNT(i) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  int countProductsByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT COALESCE(SUM(i.stockNumber), 0) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  int sumStockByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT COUNT(i) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId AND i.stockLevel = 'LOW'")
  int countLowStockByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT COUNT(i) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId AND i.stockLevel = 'HIGH'")
  int countHighStockByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT SUM(ip.stockNumber) FROM InventoryProduct ip "
          + "WHERE ip.inventory.branch.branchId = :branchId "
          + "AND ip.product.productId = :productId AND ip.isActive = true")
  Integer getTotalStockByBranchAndProduct(
      @Param("branchId") Long branchId, @Param("productId") Long productId);

  @Query("SELECT ip FROM InventoryProduct ip WHERE ip.expDate >= :date AND ip.isDiscounted = false")
  List<InventoryProduct> findByExpDateGreaterThanEqualAndIsDiscountedFalse(
      @Param("date") java.time.LocalDate date);

  @Query("SELECT ip FROM InventoryProduct ip WHERE ip.expDate <= :date AND ip.stockLevel = 'LOW'")
  List<InventoryProduct> findExpiringLowStock(@Param("date") java.time.LocalDateTime date);

  @Query(
      "SELECT ip FROM InventoryProduct ip WHERE ip.inventory.inventoryId = :inventoryId AND ip.isDiscounted = true")
  Page<InventoryProduct> findByInventory_InventoryIdAndIsDiscountedTrue(
      @Param("inventoryId") Long inventoryId, Pageable pageable);

  Optional<InventoryProduct>
      findFirstByInventory_Branch_BranchIdAndProduct_ProductIdOrderByExpDateAsc(
          Long branchId, Long productId);

  Optional<InventoryProduct> findFirstByProduct_ProductId(Long productId);

  @Query(
      "SELECT SUM(ip.stockNumber) FROM InventoryProduct ip WHERE ip.product.productId = :productId")
  Integer totalStockByProductId(@Param("productId") Long productId);
}
