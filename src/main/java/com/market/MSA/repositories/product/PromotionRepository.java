package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Promotion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

  @Query(
      """
	SELECT p FROM Promotion p
	WHERE (:keyword IS NULL OR LOWER(p.productMain.name) LIKE LOWER(CONCAT('%',:keyword,'%'))
			OR LOWER(p.productFree.name) LIKE LOWER(CONCAT('%',:keyword,'%')))
		AND (:status IS NULL OR p.status = :status)
	""")
  @EntityGraph(attributePaths = {"productMain", "productFree"})
  List<Promotion> filter(
      @Param("keyword") String keyword,
      @Param("status") com.market.MSA.constants.PromocodeStatus status,
      Sort sort);

  @Query(
      """
	SELECT p FROM Promotion p
	WHERE (:keyword IS NULL OR LOWER(p.productMain.name) LIKE LOWER(CONCAT('%',:keyword,'%'))
			OR LOWER(p.productFree.name) LIKE LOWER(CONCAT('%',:keyword,'%')))
		AND (:status IS NULL OR p.status = :status)
	""")
  @EntityGraph(attributePaths = {"productMain", "productFree"})
  Page<Promotion> filterWithPaging(
      @Param("keyword") String keyword,
      @Param("status") com.market.MSA.constants.PromocodeStatus status,
      Pageable pageable);

  /** Find active promotions for a main product within current date range. */
  @Query(
      """
	SELECT p FROM Promotion p
	WHERE p.productMain.productId = :productMainId
			AND p.startDate <= :now
			AND p.endDate   >= :now
			AND p.status = com.market.MSA.constants.PromocodeStatus.ACTIVE
	""")
  @EntityGraph(attributePaths = {"productMain", "productFree"})
  List<Promotion> findActiveByProductMain(
      @Param("productMainId") Long productMainId, @Param("now") LocalDateTime now);

  boolean existsByProductMain_ProductIdAndProductFree_ProductId(
      Long productMainId, Long productFreeId);
}
