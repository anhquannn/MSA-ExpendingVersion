package com.market.MSA.repositories.product;

import com.market.MSA.models.product.InventoryProduct;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {
  @Query("SELECT i FROM InventoryProduct i WHERE i.product.productId = :productId")
  List<InventoryProduct> findByProductId_ProductId(@Param("productId") Long productId);

  @Query("SELECT i FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  Page<InventoryProduct> findByInventory_InventoryIdWithPageable(
      @Param("inventoryId") Long inventoryId, Pageable pageable);

  @Query("SELECT i FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  List<InventoryProduct> findAllByInventory_InventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT i FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId AND i.product.productId = :productId")
  List<InventoryProduct> findByInventory_InventoryIdAndProduct_ProductId(
      @Param("inventoryId") Long inventoryId, @Param("productId") Long productId);

  @Query("SELECT COUNT(i) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  int countProductsByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT COALESCE(SUM(i.stockNumber), 0) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  int sumStockByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT COUNT(i) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId AND i.stockLevel = 'low'")
  int countLowStockByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT COUNT(i) FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId AND i.stockLevel = 'high'")
  int countHighStockByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT SUM(ip.stockNumber) FROM InventoryProduct ip "
          + "WHERE ip.inventory.branch.branchId = :branchId "
          + "AND ip.product.productId = :productId")
  Integer getTotalStockByBranchAndProduct(
      @Param("branchId") Long branchId, @Param("productId") Long productId);
}
