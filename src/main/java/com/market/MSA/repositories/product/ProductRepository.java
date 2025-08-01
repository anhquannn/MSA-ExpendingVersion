package com.market.MSA.repositories.product;

import com.market.MSA.constants.ABCClassification;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.Supplier;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "JOIN p.inventoryProducts ip "
          + "WHERE ip.inventory.branch.branchId = :branchId "
          + "AND (COALESCE(:categoryIds, NULL) IS NULL OR p.category.categoryId IN :categoryIds) "
          + "AND (:supplierId IS NULL OR p.supplier.supplierId = :supplierId) "
          + "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  @EntityGraph(attributePaths = {"category", "supplier"})
  Page<Product> findByBranchAndFilters(
      @Param("branchId") Long branchId,
      @Param("categoryIds") List<Long> categoryIds,
      @Param("supplierId") Long supplierId,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "JOIN FETCH p.supplier s "
          + "JOIN FETCH p.category c "
          + "LEFT JOIN p.inventoryProducts ip "
          + "WHERE (:branchId IS NULL OR ip.inventory.branch.branchId = :branchId) "
          + "AND (COALESCE(:categoryIds, NULL) IS NULL OR c.categoryId IN :categoryIds OR c.parentCategory.categoryId IN :categoryIds) "
          + "AND (:supplierId IS NULL OR s.supplierId = :supplierId) "
          + "AND (:unit IS NULL OR p.unit = :unit) "
          + "AND (:netWeight IS NULL OR p.netWeight = :netWeight) "
          + "AND (:minPrice IS NULL OR p.price >= :minPrice) "
          + "AND (:maxPrice IS NULL OR p.price <= :maxPrice) "
          + "AND (:abcClassification IS NULL OR p.abcClassification = :abcClassification) "
          + "AND (:isPromotional IS NULL OR p.isPromotional = :isPromotional) "
          + "AND (:isExemptFromPromotion IS NULL OR p.isExemptFromPromotion = :isExemptFromPromotion) "
          + "AND (:fromDate IS NULL OR p.createdAt >= :fromDate) "
          + "AND (:toDate IS NULL OR p.createdAt <= :toDate) "
          + "AND (:keyword IS NULL OR :keyword = '' "
          + "     OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "     OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "     OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (COALESCE(:excludeProductIds, NULL) IS NULL OR p.productId NOT IN :excludeProductIds)")
  @EntityGraph(attributePaths = {"category", "supplier"})
  Page<Product> filterWithPaging(
      @Param("branchId") Long branchId,
      @Param("categoryIds") List<Long> categoryIds,
      @Param("supplierId") Long supplierId,
      @Param("unit") String unit,
      @Param("netWeight") String netWeight,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("keyword") String keyword,
      @Param("excludeProductIds") List<Long> excludeProductIds,
      @Param("abcClassification") String abcClassification,
      @Param("isPromotional") Boolean isPromotional,
      @Param("isExemptFromPromotion") Boolean isExemptFromPromotion,
      Pageable pageable);

  @Query(
      "select distinct p.unit from Product p where p.category.categoryId = :categoryId and p.unit is not null")
  List<String> findDistinctUnitsByCategoryId(@Param("categoryId") Long categoryId);

  @Query(
      "select distinct p.netWeight from Product p where p.category.categoryId = :categoryId and p.netWeight is not null")
  List<String> findDistinctNetWeightsByCategoryId(@Param("categoryId") Long categoryId);

  // Best selling A-class products by totalRevenue desc
  @Query(
      "SELECT p FROM Product p WHERE p.abcClassification = com.market.MSA.constants.ABCClassification.A ORDER BY p.totalRevenue DESC")
  Page<Product> findBestSellingAProducts(Pageable pageable);

  List<Product> findByAbcClassification(
      com.market.MSA.constants.ABCClassification abcClassification);

  @Query(
      "SELECT p FROM Product p WHERE p.abcClassification = :abc AND p.isExemptFromPromotion = false")
  List<Product> findByABCAndNotExempt(@Param("abc") ABCClassification abc);

  @Query("select distinct p.supplier from Product p where p.category.categoryId = :categoryId")
  List<Supplier> findDistinctSuppliersByCategoryId(@Param("categoryId") Long categoryId);
}
