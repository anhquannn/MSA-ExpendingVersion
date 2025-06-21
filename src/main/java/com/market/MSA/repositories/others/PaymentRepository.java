package com.market.MSA.repositories.others;

import com.market.MSA.models.others.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  // --- Có phân trang: lọc theo userId + orderId + status + paymentMethod + từ ngày đến ngày ---
  @Query(
      "SELECT p FROM Payment p WHERE "
          + "(:userId IS NULL OR p.user.userId = :userId) AND "
          + "(:orderId IS NULL OR p.order.orderId = :orderId) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) AND "
          + "(:fromDate IS NULL OR p.paymentDate >= :fromDate) AND "
          + "(:toDate IS NULL OR p.paymentDate <= :toDate)")
  Page<Payment> filterWithPaging(
      @Param("userId") Long userId,
      @Param("orderId") Long orderId,
      @Param("status") String status,
      @Param("paymentMethod") String paymentMethod,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  // --- Không phân trang (dạng list): hỗ trợ sort ---
  @Query(
      "SELECT p FROM Payment p WHERE "
          + "(:userId IS NULL OR p.user.userId = :userId) AND "
          + "(:orderId IS NULL OR p.order.orderId = :orderId) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) AND "
          + "(:fromDate IS NULL OR p.paymentDate >= :fromDate) AND "
          + "(:toDate IS NULL OR p.paymentDate <= :toDate)")
  List<Payment> filter(
      @Param("userId") Long userId,
      @Param("orderId") Long orderId,
      @Param("status") String status,
      @Param("paymentMethod") String paymentMethod,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);

  Optional<Payment> findByTransactionId(String transactionId);
}
