package com.market.MSA.repositories.order;

import com.market.MSA.constants.ReturnStatus;
import com.market.MSA.models.order.ReturnOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {

  @Query(
      "SELECT ro FROM ReturnOrder ro WHERE "
          + "(:status IS NULL OR ro.status = :status) AND "
          + "(:userId IS NULL OR ro.user.userId = :userId) AND "
          + "(:orderId IS NULL OR ro.order.orderId = :orderId) AND "
          + "(:branchId IS NULL OR ro.order.branch.branchId = :branchId) AND "
          + "(:fromDate IS NULL OR ro.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR ro.createdAt <= :toDate)")
  @EntityGraph(
      attributePaths = {
        "order",
        "user",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  Page<ReturnOrder> filterWithPaging(
      @Param("status") ReturnStatus status,
      @Param("userId") Long userId,
      @Param("orderId") Long orderId,
      @Param("branchId") Long branchId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT ro FROM ReturnOrder ro WHERE "
          + "(:status IS NULL OR ro.status = :status) AND "
          + "(:userId IS NULL OR ro.user.userId = :userId) AND "
          + "(:orderId IS NULL OR ro.order.orderId = :orderId) AND "
          + "(:branchId IS NULL OR ro.order.branch.branchId = :branchId) AND "
          + "(:fromDate IS NULL OR ro.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR ro.createdAt <= :toDate)")
  @EntityGraph(
      attributePaths = {
        "order",
        "user",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  List<ReturnOrder> filter(
      @Param("status") ReturnStatus status,
      @Param("userId") Long userId,
      @Param("orderId") Long orderId,
      @Param("branchId") Long branchId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  // Find return orders by user
  @EntityGraph(
      attributePaths = {
        "order",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  List<ReturnOrder> findByUserUserId(Long userId);

  // Find return orders by order
  @EntityGraph(
      attributePaths = {
        "user",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  List<ReturnOrder> findByOrderOrderId(Long orderId);

  // Find return orders by status
  @EntityGraph(
      attributePaths = {
        "order",
        "user",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  List<ReturnOrder> findByStatus(ReturnStatus status);

  // Find return orders by branch (through order)
  @Query("SELECT ro FROM ReturnOrder ro WHERE ro.order.branch.branchId = :branchId")
  @EntityGraph(
      attributePaths = {
        "order",
        "user",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  List<ReturnOrder> findByBranchId(@Param("branchId") Long branchId);

  // Count return orders by status
  long countByStatus(ReturnStatus status);

  // Count return orders by user
  long countByUserUserId(Long userId);

  // Find pending return orders for a specific branch
  @Query(
      "SELECT ro FROM ReturnOrder ro WHERE ro.order.branch.branchId = :branchId AND ro.status = :status")
  @EntityGraph(
      attributePaths = {
        "order",
        "user",
        "returnOrderItems",
        "returnOrderItems.orderDetail",
        "returnOrderItems.images"
      })
  List<ReturnOrder> findByBranchIdAndStatus(
      @Param("branchId") Long branchId, @Param("status") ReturnStatus status);
}
