package com.market.MSA.repositories.order;

import com.market.MSA.models.order.CancelOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CancelOrderRepository extends JpaRepository<CancelOrder, Long> {
  // Có phân trang
  @Query(
      "SELECT c FROM CancelOrder c WHERE "
          + "(:orderId IS NULL OR c.order.id = :orderId) AND "
          + "(:userId IS NULL OR c.order.user.userId = :userId) AND "
          + "(:status IS NULL OR c.status = :status) AND "
          + "(:reason IS NULL OR LOWER(c.reason) LIKE LOWER(CONCAT('%', :reason, '%'))) AND "
          + "(:fromDate IS NULL OR c.cancelDate >= :fromDate) AND "
          + "(:toDate IS NULL OR c.cancelDate <= :toDate)")
  Page<CancelOrder> filterWithPaging(
      @Param("orderId") Long orderId,
      @Param("userId") Long userId,
      @Param("status") String status,
      @Param("reason") String reason,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  // Không phân trang
  @Query(
      "SELECT c FROM CancelOrder c WHERE "
          + "(:orderId IS NULL OR c.order.id = :orderId) AND "
          + "(:userId IS NULL OR c.order.user.userId = :userId) AND "
          + "(:status IS NULL OR c.status = :status) AND "
          + "(:reason IS NULL OR LOWER(c.reason) LIKE LOWER(CONCAT('%', :reason, '%'))) AND "
          + "(:fromDate IS NULL OR c.cancelDate >= :fromDate) AND "
          + "(:toDate IS NULL OR c.cancelDate <= :toDate)")
  List<CancelOrder> filter(
      @Param("orderId") Long orderId,
      @Param("userId") Long userId,
      @Param("status") String status,
      @Param("reason") String reason,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);
}
