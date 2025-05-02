package com.market.MSA.repositories.order;

import com.market.MSA.models.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
  // Tham chiếu đến thuộc tính user của Order
  Page<Order> findByUser_UserIdAndStatus(Long userId, String status, Pageable pageable);

  // Tham chiếu đến thuộc tính user của Order để tìm theo số điện thoại
  Page<Order> findByUser_PhoneNumber(String phoneNumber, Pageable pageable);

  @Query("SELECT o FROM Order o WHERE o.branch.branchId = :branchId")
  Page<Order> findByBranch_BranchId(@Param("branchId") Long branchId, Pageable pageable);

  @Query(
      "SELECT o FROM Order o JOIN o.user u WHERE o.branch.branchId = :branchId ORDER BY u.phoneNumber")
  Page<Order> findByBranch_BranchIdWithUserSort(
      @Param("branchId") Long branchId, Pageable pageable);

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
}
