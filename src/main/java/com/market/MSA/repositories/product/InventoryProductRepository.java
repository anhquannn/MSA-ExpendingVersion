package com.market.MSA.repositories.product;

import com.market.MSA.models.product.InventoryProduct;
import java.time.LocalDateTime;
import java.util.List;
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

  /**
   * Find all inventory products that expire on or after the given date and are not yet discounted.
   *
   * @param date The minimum expiration date to check
   * @return List of matching inventory products
   */
  @Query("SELECT ip FROM InventoryProduct ip WHERE ip.expDate >= :date AND ip.isDiscounted = false")
  List<InventoryProduct> findByExpDateGreaterThanEqualAndIsDiscountedFalse(
      @Param("date") java.time.LocalDate date);

  /**
   * Find paginated inventory products that are discounted for a specific inventory.
   *
   * @param inventoryId The ID of the inventory to search in
   * @param pageable Pagination information
   * @return Page of discounted inventory products
   */
  @Query(
      "SELECT ip FROM InventoryProduct ip WHERE ip.inventory.inventoryId = :inventoryId AND ip.isDiscounted = true")
  Page<InventoryProduct> findByInventory_InventoryIdAndIsDiscountedTrue(
      @Param("inventoryId") Long inventoryId, Pageable pageable);
}
