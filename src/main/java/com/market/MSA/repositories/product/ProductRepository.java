package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Product;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "JOIN p.inventoryProducts ip "
          + "WHERE ip.inventory.branch.branchId = :branchId "
          + "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId) "
          + "AND (:supplierId IS NULL OR p.supplier.supplierId = :supplierId) "
          + "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Product> findByBranchAndFilters(
      @Param("branchId") Long branchId,
      @Param("categoryId") Long categoryId,
      @Param("supplierId") Long supplierId,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "JOIN FETCH p.supplier s "
          + "JOIN FETCH p.category c "
          + "LEFT JOIN p.inventoryProducts ip "
          + "WHERE (:branchId IS NULL OR ip.inventory.branch.branchId = :branchId) "
          + "AND (:categoryId IS NULL OR c.categoryId = :categoryId OR c.parentCategory.categoryId = :categoryId) "
          + "AND (:supplierId IS NULL OR s.supplierId = :supplierId) "
          + "AND (:unit IS NULL OR p.unit = :unit) "
          + "AND (:netWeight IS NULL OR p.netWeight = :netWeight) "
          + "AND (:minPrice IS NULL OR p.price >= :minPrice) "
          + "AND (:maxPrice IS NULL OR p.price <= :maxPrice) "
          + "AND (:fromDate IS NULL OR p.createdAt >= :fromDate) "
          + "AND (:toDate IS NULL OR p.createdAt <= :toDate) "
          + "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (COALESCE(:excludeProductIds, NULL) IS NULL OR p.productId NOT IN :excludeProductIds)")
  Page<Product> filterWithPaging(
      @Param("branchId") Long branchId,
      @Param("categoryId") Long categoryId,
      @Param("supplierId") Long supplierId,
      @Param("unit") String unit,
      @Param("netWeight") String netWeight,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("keyword") String keyword,
      @Param("excludeProductIds") List<Long> excludeProductIds,
      Pageable pageable);
}
