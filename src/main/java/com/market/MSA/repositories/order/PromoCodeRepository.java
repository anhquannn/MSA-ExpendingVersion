package com.market.MSA.repositories.order;

import com.market.MSA.models.order.PromoCode;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

  Optional<PromoCode> findByCode(String code);

  @Modifying
  @Transactional
  @Query(
      "UPDATE PromoCode p SET p.status = 'active' WHERE p.startDate <= :currentDate AND p.endDate > :currentDate AND p.status != 'active'")
  void updateActivePromoCodes(Date currentDate);

  @Modifying
  @Transactional
  @Query(
      "UPDATE PromoCode p SET p.status = 'expired' WHERE p.endDate <= :currentDate AND p.status != 'expired'")
  void updateExpiredPromoCodes(Date currentDate);
}
