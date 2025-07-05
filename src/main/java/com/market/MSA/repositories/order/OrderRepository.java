package com.market.MSA.repositories.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.product.Branch;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @Query(
      "SELECT o FROM Order o WHERE "
          + "(:status IS NULL OR o.status = :status) AND "
          + "(:userId IS NULL OR o.user.userId = :userId) AND "
          + "(:branchId IS NULL OR o.branch.branchId = :branchId) AND "
          + "(:phoneNumber IS NULL OR o.user.phoneNumber = :phoneNumber) AND "
          + "(:fromDate IS NULL OR o.orderDate >= :fromDate) AND "
          + "(:toDate IS NULL OR o.orderDate <= :toDate)")
  Page<Order> filterWithPaging(
      @Param("status") OrderStatus status,
      @Param("userId") Long userId,
      @Param("branchId") Long branchId,
      @Param("phoneNumber") String phoneNumber,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT o FROM Order o WHERE "
          + "(:status IS NULL OR o.status = :status) AND "
          + "(:userId IS NULL OR o.user.userId = :userId) AND "
          + "(:branchId IS NULL OR o.branch.branchId = :branchId) AND "
          + "(:phoneNumber IS NULL OR o.user.phoneNumber = :phoneNumber) AND "
          + "(:fromDate IS NULL OR o.orderDate >= :fromDate) AND "
          + "(:toDate IS NULL OR o.orderDate <= :toDate)")
  List<Order> filter(
      @Param("status") OrderStatus status,
      @Param("userId") Long userId,
      @Param("branchId") Long branchId,
      @Param("phoneNumber") String phoneNumber,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
  Double calculateMonthlyRevenue(@Param("year") int year, @Param("month") int month);

  @Query("SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year")
  Double calculateYearlyRevenue(@Param("year") int year);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.branch.branchId = :branchId")
  Double calculateMonthlyRevenueByBranch(
      @Param("year") int year, @Param("month") int month, @Param("branchId") Long branchId);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND o.branch.branchId = :branchId")
  Double calculateYearlyRevenueByBranch(@Param("year") int year, @Param("branchId") Long branchId);

  @Query(
      "SELECT COUNT(o) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
  Long countByMonth(@Param("year") int year, @Param("month") int month);

  @Query(
      "SELECT COUNT(o) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.branch.branchId = :branchId")
  Long countByMonthAndBranch(
      @Param("year") int year, @Param("month") int month, @Param("branchId") Long branchId);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.user.userId = :userId")
  Double calculateMonthlyRevenueByUser(
      @Param("year") int year, @Param("month") int month, @Param("userId") Long userId);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND o.user.userId = :userId")
  Double calculateYearlyRevenueByUser(@Param("year") int year, @Param("userId") Long userId);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.branch.branchId = :branchId AND o.user.userId = :userId")
  Double calculateMonthlyRevenueByBranchAndUser(
      @Param("year") int year,
      @Param("month") int month,
      @Param("branchId") Long branchId,
      @Param("userId") Long userId);

  @Query(
      "SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE YEAR(o.orderDate) = :year AND o.branch.branchId = :branchId AND o.user.userId = :userId")
  Double calculateYearlyRevenueByBranchAndUser(
      @Param("year") int year, @Param("branchId") Long branchId, @Param("userId") Long userId);

  Long branch(Branch branch);

  @Query(
      """
      SELECT o.user.userId, o.user.fullName, SUM(o.grandTotal) AS total
      FROM Order o
      WHERE o.status = com.market.MSA.constants.OrderStatus.COMPLETED
      AND o.orderDate BETWEEN :start AND :end
      AND (:branchId IS NULL OR o.branch.branchId = :branchId)
      GROUP BY o.user.userId, o.user.fullName
      ORDER BY total DESC
      """)
  List<Object[]> findTopCustomers(
      @Param("branchId") Long branchId,
      @Param("start") java.time.LocalDateTime start,
      @Param("end") java.time.LocalDateTime end,
      org.springframework.data.domain.Pageable pageable);

}
