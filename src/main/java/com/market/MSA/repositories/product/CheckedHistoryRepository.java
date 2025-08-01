package com.market.MSA.repositories.product;

import com.market.MSA.models.product.CheckedHistory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckedHistoryRepository extends JpaRepository<CheckedHistory, Long> {
  // List - không phân trang
  @Query(
      "SELECT c FROM CheckedHistory c WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR LOWER(c.note) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:inventoryId IS NULL OR c.inventory.inventoryId = :inventoryId) "
          + "AND (:userId IS NULL OR c.user.userId = :userId)")
  @EntityGraph(attributePaths = {"inventory", "user"})
  List<CheckedHistory> filter(
      @Param("keyword") String keyword,
      @Param("inventoryId") Long inventoryId,
      @Param("userId") Long userId);

  // Page - có phân trang
  @Query(
      "SELECT c FROM CheckedHistory c WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR LOWER(c.note) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:inventoryId IS NULL OR c.inventory.inventoryId = :inventoryId) "
          + "AND (:userId IS NULL OR c.user.userId = :userId)")
  @EntityGraph(attributePaths = {"inventory", "user"})
  Page<CheckedHistory> filterWithPaging(
      @Param("keyword") String keyword,
      @Param("inventoryId") Long inventoryId,
      @Param("userId") Long userId,
      Pageable pageable);
}
