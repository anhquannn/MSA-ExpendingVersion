package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query(
      "SELECT p FROM Product p "
          + "WHERE (:branchId IS NULL OR p IN (SELECT ip.product FROM InventoryProduct ip WHERE ip.inventory.branch.branchId = :branchId)) "
          + "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Product> searchByKeyword(
      @Param("branchId") Long branchId, @Param("keyword") String keyword, Pageable pageable);

  @Query(
      "SELECT p FROM Product p "
          + "WHERE p IN (SELECT ip.product FROM InventoryProduct ip WHERE ip.inventory.branch.branchId = :branchId) "
          + "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:minPrice IS NULL OR p.currentPrice >= :minPrice) "
          + "AND (:maxPrice IS NULL OR p.currentPrice <= :maxPrice) "
          + "AND (:color IS NULL OR LOWER(p.color) = LOWER(:color)) ")
  Page<Product> findByBranchAndFilters(
      @Param("branchId") Long branchId,
      @Param("keyword") String keyword,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("color") String color,
      Pageable pageable);

  @Query(
      "SELECT p FROM Product p "
          + "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:minPrice IS NULL OR p.currentPrice >= :minPrice) "
          + "AND (:maxPrice IS NULL OR p.currentPrice <= :maxPrice) "
          + "AND (:color IS NULL OR LOWER(p.color) = LOWER(:color)) "
          + "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId) "
          + "AND (:manufacturerId IS NULL OR p.manufacturer.manufacturerId = :manufacturerId)")
  Page<Product> searchProducts(
      @Param("keyword") String keyword,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("color") String color,
      @Param("categoryId") Long categoryId,
      @Param("manufacturerId") Long manufacturerId,
      Pageable pageable);
}
