package com.market.MSA.repositories.order;

import com.market.MSA.models.order.OrderDetail;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

  @EntityGraph(attributePaths = {"product", "order", "order.branch"})
  List<OrderDetail> findByOrder_OrderId(long orderId);

  @Query(
      "SELECT YEAR(o.orderDate) AS year, MONTH(o.orderDate) AS month, SUM(od.quantity) AS qty "
          + "FROM OrderDetail od JOIN od.order o "
          + "WHERE od.product.productId = :productId "
          + "AND o.orderDate BETWEEN :startDate AND :endDate "
          + "AND (:branchId IS NULL OR o.branch.branchId = :branchId) "
          + "AND o.status = com.market.MSA.constants.OrderStatus.COMPLETED "
          + "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) "
          + "ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)")
  List<Object[]> findMonthlySalesByProduct(
      @Param("productId") Long productId,
      @Param("branchId") Long branchId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      """
	SELECT YEAR(o.orderDate) , MONTH(o.orderDate) , SUM(od.quantity * od.unitPrice)
	FROM OrderDetail od JOIN od.order o
	WHERE o.orderDate BETWEEN :start AND :end
	AND (:branchId IS NULL OR o.branch.branchId = :branchId)
	AND o.status = com.market.MSA.constants.OrderStatus.COMPLETED
	GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)
	ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)
	""")
  List<Object[]> findMonthlyRevenue(
      @Param("branchId") Long branchId,
      @Param("start") java.time.LocalDateTime start,
      @Param("end") java.time.LocalDateTime end);

  @Query(
      """
	SELECT o.branch.branchId, o.branch.name, SUM(od.quantity * od.unitPrice) AS total
	FROM OrderDetail od JOIN od.order o
	WHERE o.orderDate BETWEEN :start AND :end
	AND o.status = com.market.MSA.constants.OrderStatus.COMPLETED
	GROUP BY o.branch.branchId, o.branch.name
	ORDER BY total DESC
	""")
  List<Object[]> findRevenueByBranch(
      @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

  @Query(
      "SELECT od.product.productId, od.product.name, SUM(od.quantity * od.unitPrice) AS qty "
          + "FROM OrderDetail od JOIN od.order o "
          + "WHERE o.orderDate BETWEEN :startDate AND :endDate "
          + "AND (:branchId IS NULL OR o.branch.branchId = :branchId) "
          + "AND o.status = com.market.MSA.constants.OrderStatus.COMPLETED "
          + "GROUP BY od.product.productId, od.product.name "
          + "ORDER BY qty DESC")
  List<Object[]> findTopSellingProducts(
      @Param("branchId") Long branchId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      org.springframework.data.domain.Pageable pageable);

  /** Get total revenue per product for a given period. */
  @Query(
      """
	SELECT od.product.productId, SUM(od.quantity * od.unitPrice)
	FROM OrderDetail od JOIN od.order o
	WHERE o.orderDate BETWEEN :start AND :end
		AND o.status = com.market.MSA.constants.OrderStatus.COMPLETED
	GROUP BY od.product.productId
	""")
  List<Object[]> findRevenuePerProduct(
      @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
