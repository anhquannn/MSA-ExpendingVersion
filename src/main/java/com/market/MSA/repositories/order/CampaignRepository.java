package com.market.MSA.repositories.order;

import com.market.MSA.models.order.Campaign;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

  @Query(
      "SELECT c FROM Campaign c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  Page<Campaign> searchByName(@Param("name") String name, Pageable pageable);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Campaign c SET c.status = 'active' WHERE c.startDate <= :currentDate AND c.endDate > :currentDate AND c.status != 'active'")
  void updateActiveCampaigns(Date currentDate);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Campaign c SET c.status = 'expired' WHERE c.endDate <= :currentDate AND c.status != 'expired'")
  void updateExpiredCampaigns(Date currentDate);
}
