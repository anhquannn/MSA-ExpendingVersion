package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Feedback;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  boolean existsByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

  @Query(
      "SELECT f FROM Feedback f WHERE "
          + "(:productId IS NULL OR f.product.productId = :productId) AND "
          + "(:userId IS NULL OR f.user.userId = :userId) AND "
          + "(:minRating IS NULL OR f.rating >= :minRating) AND "
          + "(:maxRating IS NULL OR f.rating <= :maxRating) AND "
          + "(:fromDate IS NULL OR f.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR f.createdAt <= :toDate)")
  @EntityGraph(attributePaths = {"user", "product"})
  List<Feedback> filter(
      @Param("productId") Long productId,
      @Param("userId") Long userId,
      @Param("minRating") Integer minRating,
      @Param("maxRating") Integer maxRating,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query(
      "SELECT f FROM Feedback f WHERE "
          + "(:productId IS NULL OR f.product.productId = :productId) AND "
          + "(:userId IS NULL OR f.user.userId = :userId) AND "
          + "(:minRating IS NULL OR f.rating >= :minRating) AND "
          + "(:maxRating IS NULL OR f.rating <= :maxRating) AND "
          + "(:fromDate IS NULL OR f.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR f.createdAt <= :toDate)")
  @EntityGraph(attributePaths = {"user", "product"})
  Page<Feedback> filterWithPaging(
      @Param("productId") Long productId,
      @Param("userId") Long userId,
      @Param("minRating") Integer minRating,
      @Param("maxRating") Integer maxRating,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query("SELECT f.product.productId, AVG(f.rating) from Feedback f GROUP BY f.product.productId")
  @EntityGraph(attributePaths = {"product"})
  List<Object[]> findAverageRatingsByProduct();

  boolean existsByOrderDetail_OrderDetailId(Long orderDetailId);
}
