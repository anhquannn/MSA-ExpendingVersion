package com.market.MSA.repositories.order;

import com.market.MSA.models.order.PromoCodeUsage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromoCodeUsageRepository extends JpaRepository<PromoCodeUsage, Long> {
  @Query(
      "SELECT u FROM PromoCodeUsage u WHERE "
          + "(:userId IS NULL OR u.user.userId = :userId) AND "
          + "(:promoCodeId IS NULL OR u.promoCode.promoCodeId = :promoCodeId) AND "
          + "(:fromDate IS NULL OR u.usedAt >= :fromDate) AND "
          + "(:toDate IS NULL OR u.usedAt <= :toDate)")
  Page<PromoCodeUsage> filterWithPaging(
      @Param("userId") Long userId,
      @Param("promoCodeId") Long promoCodeId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT u FROM PromoCodeUsage u WHERE "
          + "(:userId IS NULL OR u.user.userId = :userId) AND "
          + "(:promoCodeId IS NULL OR u.promoCode.promoCodeId = :promoCodeId) AND "
          + "(:fromDate IS NULL OR u.usedAt >= :fromDate) AND "
          + "(:toDate IS NULL OR u.usedAt <= :toDate)")
  List<PromoCodeUsage> filter(
      @Param("userId") Long userId,
      @Param("promoCodeId") Long promoCodeId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);

  boolean existsByUser_UserIdAndPromoCode_PromoCodeId(Long userId, Long promoCodeId);
}
