package com.market.MSA.repositories.user;

import com.market.MSA.models.user.RewardPoint;
import com.market.MSA.models.user.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    @Query("SELECT u FROM UserAddress u WHERE " + "(:userId IS NULL OR u.user.userId = :userId)")
    Page<UserAddress> filterWithPaging(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT u FROM UserAddress u WHERE " + "(:userId IS NULL OR u.user.userId = :userId)")
    List<UserAddress> filter(@Param("userId") Long userId, Sort sort);
}
