package com.market.MSA.repositories.user;

import com.market.MSA.models.user.RewardPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardPointRepository extends JpaRepository<RewardPoint, Long> {
  @Query("SELECT r FROM RewardPoint r WHERE r.user.userId = :userId")
  Page<RewardPoint> findByUser_UserId(@Param("userId") Long userId, Pageable pageable);
}
