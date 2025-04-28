package com.market.MSA.repositories.user;

import com.market.MSA.models.user.RewardPointTransaction;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardPointTransactionRepository
    extends JpaRepository<RewardPointTransaction, Long> {
  @Query("SELECT r FROM RewardPointTransaction r WHERE r.user.userId = :userId")
  Page<RewardPointTransaction> findByUser_UserId(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT r FROM RewardPointTransaction r WHERE r.order.orderId = :orderId")
  Page<RewardPointTransaction> findByOrder_OrderId(
      @Param("orderId") Long orderId, Pageable pageable);

  @Query(
      "SELECT r FROM RewardPointTransaction r WHERE r.user.userId = :userId AND r.createdAt BETWEEN :fromDate AND :toDate")
  Page<RewardPointTransaction> findByUser_UserIdAndCreatedAtBetween(
      @Param("userId") Long userId,
      @Param("fromDate") Date fromDate,
      @Param("toDate") Date toDate,
      Pageable pageable);
}
