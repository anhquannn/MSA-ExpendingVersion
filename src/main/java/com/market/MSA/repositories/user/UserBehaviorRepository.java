package com.market.MSA.repositories.user;

import com.market.MSA.models.user.UserBehavior;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
  @Query("SELECT ub FROM UserBehavior ub WHERE ub.user.userId = :userId")
  List<UserBehavior> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
