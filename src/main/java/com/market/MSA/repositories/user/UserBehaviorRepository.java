package com.market.MSA.repositories.user;

import com.market.MSA.models.user.UserBehavior;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
  @Query(
      "SELECT u FROM UserBehavior u WHERE "
          + "(:userId IS NULL OR u.user.userId = :userId) AND "
          + "(:productId IS NULL OR u.product.productId = :productId)")
  Page<UserBehavior> filterWithPaging(
      @Param("userId") Long userId, @Param("productId") Long productId, Pageable pageable);

  @Query(
      "SELECT u FROM UserBehavior u WHERE "
          + "(:userId IS NULL OR u.user.userId = :userId) AND "
          + "(:productId IS NULL OR u.product.productId = :productId)")
  List<UserBehavior> filter(
      @Param("userId") Long userId, @Param("productId") Long productId, Sort sort);
}
