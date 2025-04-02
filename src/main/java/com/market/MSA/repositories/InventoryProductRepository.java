package com.market.MSA.repositories;

import com.market.MSA.models.InventoryProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {
  @Query("SELECT i FROM InventoryProduct i WHERE i.product.productId = :productId")
  List<InventoryProduct> findByProductId_ProductId(@Param("productId") Long productId);

  @Query("SELECT i FROM InventoryProduct i WHERE i.inventory.inventoryId = :inventoryId")
  List<InventoryProduct> findByInventory_InventoryId(@Param("inventoryId") Long inventoryId);
}
