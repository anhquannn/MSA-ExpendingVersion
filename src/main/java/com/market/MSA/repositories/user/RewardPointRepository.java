package com.market.MSA.repositories.user;

import com.market.MSA.models.user.RewardPoint;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardPointRepository extends JpaRepository<RewardPoint, Long> {
  @Query("SELECT r FROM RewardPoint r WHERE r.user.userId = :userId")
  Page<RewardPoint> findByUser_UserId(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT r FROM RewardPoint r WHERE " + "(:userId IS NULL OR r.user.userId = :userId)")
  Page<RewardPoint> filterWithPaging(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT r FROM RewardPoint r WHERE " + "(:userId IS NULL OR r.user.userId = :userId)")
  List<RewardPoint> filter(@Param("userId") Long userId, Sort sort);
}
