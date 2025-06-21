package com.market.MSA.repositories.user;

import com.market.MSA.models.user.RewardPointTransaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardPointTransactionRepository
    extends JpaRepository<RewardPointTransaction, Long> {
  @Query(
      "SELECT r FROM RewardPointTransaction r WHERE "
          + "(:userId IS NULL OR r.user.userId = :userId) AND "
          + "(:orderId IS NULL OR r.order.orderId = :orderId) AND "
          + "(:fromDate IS NULL OR r.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR r.createdAt <= :toDate)")
  Page<RewardPointTransaction> filterWithPaging(
      @Param("userId") Long userId,
      @Param("orderId") Long orderId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT r FROM RewardPointTransaction r WHERE "
          + "(:userId IS NULL OR r.user.userId = :userId) AND "
          + "(:orderId IS NULL OR r.order.orderId = :orderId) AND "
          + "(:fromDate IS NULL OR r.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR r.createdAt <= :toDate)")
  List<RewardPointTransaction> filter(
      @Param("userId") Long userId,
      @Param("orderId") Long orderId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);
}
