package com.market.MSA.repositories.order;

import com.market.MSA.models.order.PromoCodeUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeUsageRepository extends JpaRepository<PromoCodeUsage, Long> {
  boolean existsByUser_UserIdAndPromoCode_PromoCodeId(Long userId, Long promoCodeId);
}
